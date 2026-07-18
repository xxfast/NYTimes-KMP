using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
using System.Windows.Input;
using System.Windows.Threading;
using NYTimes.Kotlin;

namespace WpfApp;

public sealed class TopStoriesViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private readonly Dispatcher _dispatcher;
    private readonly CancellationTokenSource _cancellation = new();
    private readonly WindowsTopStoriesViewModel _kotlinViewModel = new();
    private readonly Task _observation;
    private bool _isLoading = true;
    private StoryDetailViewModel? _selectedStory;
    private SectionViewModel? _selectedSection;
    private StorySummaryViewModel? _selectedArticle;
    private int _disposed;

    public TopStoriesViewModel(Dispatcher dispatcher)
    {
        _dispatcher = dispatcher;
        var storage = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "NYTimes-KMP");
        Directory.CreateDirectory(storage);
        WindowsApp.bootstrap(storage);

        RefreshCommand = new RelayCommand(_kotlinViewModel.OnRefresh);
        SelectSectionCommand = new RelayCommand<string>(_kotlinViewModel.OnSelectSection);
        OpenStoryCommand = new RelayCommand<StorySummaryViewModel>(story => _ = OpenStoryAsync(story));

        for (var index = 0; index < WindowsApp.sectionCount(); index++)
        {
            Sections.Add(new SectionViewModel(SectionName(index)));
        }

        _observation = ObserveStatesAsync();
    }

    public ObservableCollection<SectionViewModel> Sections { get; } = [];
    public ObservableCollection<StorySummaryViewModel> Articles { get; } = [];
    public ICommand RefreshCommand { get; }
    public ICommand SelectSectionCommand { get; }
    public ICommand OpenStoryCommand { get; }

    public bool IsLoading
    {
        get => _isLoading;
        private set => SetField(ref _isLoading, value);
    }

    public StoryDetailViewModel? SelectedStory
    {
        get => _selectedStory;
        private set => SetField(ref _selectedStory, value);
    }

    /// <summary>Section selected in the left navigation list.</summary>
    public SectionViewModel? SelectedSection
    {
        get => _selectedSection;
        set
        {
            if (ReferenceEquals(_selectedSection, value)) return;
            _selectedSection = value;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedSection)));
            if (value is not null) _kotlinViewModel.OnSelectSection(value.Name);
        }
    }

    /// <summary>Article selected in the story list (drives the detail pane).</summary>
    public StorySummaryViewModel? SelectedArticle
    {
        get => _selectedArticle;
        set
        {
            if (ReferenceEquals(_selectedArticle, value)) return;
            _selectedArticle = value;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedArticle)));
            if (value is not null) _ = OpenStoryAsync(value);
        }
    }

    public event PropertyChangedEventHandler? PropertyChanged;

    private async Task ObserveStatesAsync()
    {
        try
        {
            await foreach (var state in _kotlinViewModel.StateFlow.WithCancellation(_cancellation.Token))
            {
                await _dispatcher.InvokeAsync(() => Apply(state), DispatcherPriority.DataBind);
            }
        }
        catch (OperationCanceledException) when (_cancellation.IsCancellationRequested)
        {
            // Window shutdown cancels the generated KotlinFlow collection.
        }
    }

    private void Apply(WindowsTopStoriesState state)
    {
        IsLoading = state.IsLoading;

        SectionViewModel? selected = null;
        foreach (var section in Sections)
        {
            section.IsSelected = state.HasSelectedSection && section.Name == state.SectionName;
            if (section.IsSelected) selected = section;
        }

        if (!ReferenceEquals(_selectedSection, selected))
        {
            _selectedSection = selected;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedSection)));
        }

        var previousUri = _selectedArticle?.Uri;
        Articles.Clear();
        StorySummaryViewModel? restoredArticle = null;
        for (var index = 0; index < state.ArticleCount; index++)
        {
            var article = new StorySummaryViewModel(
                _kotlinViewModel.ArticleUri(index),
                _kotlinViewModel.ArticleTitle(index),
                _kotlinViewModel.ArticleDescription(index),
                _kotlinViewModel.ArticleSectionName(index),
                _kotlinViewModel.ArticleByline(index),
                _kotlinViewModel.ArticleImageUrl(index));
            Articles.Add(article);
            if (previousUri is not null && article.Uri == previousUri)
                restoredArticle = article;
        }

        // Restore list highlight after refresh without re-opening the detail pane.
        if (!ReferenceEquals(_selectedArticle, restoredArticle))
        {
            _selectedArticle = restoredArticle;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedArticle)));
        }
    }

    private static string SectionName(int index) =>
        Marshal.PtrToStringUTF8(WindowsApp.sectionName(index))!;

    private async Task OpenStoryAsync(StorySummaryViewModel story)
    {
        var previous = SelectedStory;
        var disposal = previous?.DisposeAsync().AsTask();
        SelectedStory = new StoryDetailViewModel(_dispatcher, story.SectionName, story.Uri, story.Title);
        if (disposal is not null) await disposal;
    }

    public async ValueTask DisposeAsync()
    {
        if (Interlocked.Exchange(ref _disposed, 1) != 0) return;

        var storyDisposal = SelectedStory?.DisposeAsync().AsTask();
        _cancellation.Cancel();
        _kotlinViewModel.Close();
        try { await _observation; }
        catch (OperationCanceledException) { }
        finally
        {
            await _kotlinViewModel.DisposeAsync();
            _cancellation.Dispose();
        }

        if (storyDisposal is not null) await storyDisposal;
    }

    private void SetField<T>(ref T field, T value, [CallerMemberName] string? propertyName = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return;
        field = value;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
    }
}

public sealed class StorySummaryViewModel(
    string uri,
    string title,
    string description,
    string sectionName,
    string byline,
    string imageUrl)
{
    public string Uri { get; } = uri;
    public string Title { get; } = title;
    public string Description { get; } = description;
    public string SectionName { get; } = sectionName;
    public string Byline { get; } = byline;
    public string ImageUrl { get; } = imageUrl;
}

public sealed class SectionViewModel(string name) : INotifyPropertyChanged
{
    private bool _isSelected;
    public string Name { get; } = name;

    public bool IsSelected
    {
        get => _isSelected;
        set
        {
            if (_isSelected == value) return;
            _isSelected = value;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(IsSelected)));
        }
    }

    public event PropertyChangedEventHandler? PropertyChanged;
}

public sealed class RelayCommand(Action action) : ICommand
{
    public event EventHandler? CanExecuteChanged { add { } remove { } }
    public bool CanExecute(object? parameter) => true;
    public void Execute(object? parameter) => action();
}

public sealed class RelayCommand<T>(Action<T> action) : ICommand
{
    public event EventHandler? CanExecuteChanged { add { } remove { } }
    public bool CanExecute(object? parameter) => parameter is T;
    public void Execute(object? parameter)
    {
        if (parameter is T value) action(value);
    }
}
