using System.Collections.ObjectModel;
using System.ComponentModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using NYTimes.Kotlin.Screens.TopStories;
using KotlinApp = NYTimes.Kotlin.Windows;

namespace NYTimes.Windows;

public sealed class TopStoriesViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private const string FavouritesSectionName = "favourites";

    private readonly SynchronizationContext _ui;
    private readonly CancellationTokenSource _cancellation = new();
    private readonly KotlinApp.TopStoriesViewModel _kotlinViewModel = new();
    private readonly Task _observation;
    private bool _isLoading = true;
    private string? _errorTitle;
    private string? _error;
    private bool _isEmpty;
    private string _emptyMessage = string.Empty;
    private StoryDetailViewModel? _selectedStory;
    private SectionViewModel? _selectedSection;
    private StorySummaryViewModel? _selectedArticle;
    private int _disposed;

    /// <param name="uiContext">
    /// UI synchronization context. Defaults to <see cref="SynchronizationContext.Current"/> —
    /// construct on the UI thread (WPF or WinUI) or pass one explicitly.
    /// </param>
    public TopStoriesViewModel(SynchronizationContext? uiContext = null)
    {
        _ui = uiContext ?? SynchronizationContext.Current
            ?? throw new InvalidOperationException(
                "Create on the UI thread or pass a SynchronizationContext.");

        var storage = Path.Combine(
            Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
            "NYTimes-KMP");
        Directory.CreateDirectory(storage);
        KotlinApp.WindowsApp.Bootstrap(storage);

        RefreshCommand = new RelayCommand(_kotlinViewModel.OnRefresh);
        SelectSectionCommand = new RelayCommand<string>(_kotlinViewModel.OnSelectSection);
        OpenStoryCommand = new RelayCommand<StorySummaryViewModel>(story => _ = OpenStoryAsync(story));

        foreach (var name in KotlinApp.WindowsApp.SectionNames())
        {
            Sections.Add(new SectionViewModel(name));
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

    /// <summary>Short heading for the failure, e.g. "You're offline"; null unless <see cref="HasError"/>.</summary>
    public string? ErrorTitle
    {
        get => _errorTitle;
        private set => SetField(ref _errorTitle, value);
    }

    /// <summary>Why the last load failed; null while loading or once articles arrive.</summary>
    public string? Error
    {
        get => _error;
        private set
        {
            if (!SetField(ref _error, value)) return;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(HasError)));
        }
    }

    public bool HasError => Error is not null;

    /// <summary>The section loaded successfully but has no stories.</summary>
    public bool IsEmpty
    {
        get => _isEmpty;
        private set => SetField(ref _isEmpty, value);
    }

    /// <summary>Copy for the empty state; empty string unless <see cref="IsEmpty"/>.</summary>
    public string EmptyMessage
    {
        get => _emptyMessage;
        private set => SetField(ref _emptyMessage, value);
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
                using (state)
                {
                    await RunOnUiAsync(() => Apply(state));
                }
            }
        }
        catch (OperationCanceledException) when (_cancellation.IsCancellationRequested)
        {
            // Window shutdown cancels the generated KotlinFlow collection.
        }
    }

    private void Apply(TopStoriesState state)
    {
        // null articles = shared Loading, unless the domain reported why they never arrived.
        using var failure = state.Failure;
        ErrorTitle = failure?.Title;
        Error = failure?.Message;
        IsLoading = state.Articles is null && failure is null;

        var sectionName = state.Section?.Name;
        SectionViewModel? selected = null;
        foreach (var section in Sections)
        {
            section.IsSelected = sectionName is not null && section.Name == sectionName;
            if (section.IsSelected) selected = section;
            // Only the favourites section (TopStorySections.favourites) carries a count.
            section.Count = section.Name == FavouritesSectionName ? state.NumberOfFavourites : null;
        }

        if (!ReferenceEquals(_selectedSection, selected))
        {
            _selectedSection = selected;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedSection)));
        }

        var previousUri = _selectedArticle?.Uri;
        Articles.Clear();
        StorySummaryViewModel? restoredArticle = null;
        foreach (var article in state.Articles ?? [])
        {
            using (article)
            {
                var summary = new StorySummaryViewModel(
                    article.Uri.Value,
                    article.Title,
                    article.Description,
                    article.Section.Name,
                    article.Byline,
                    article.ImageUrl ?? string.Empty);
                Articles.Add(summary);
                if (previousUri is not null && summary.Uri == previousUri)
                    restoredArticle = summary;
            }
        }

        // null = still loading; an empty list means the section really has nothing.
        IsEmpty = state.Articles is not null && Articles.Count == 0;
        EmptyMessage = IsEmpty && sectionName is not null
            ? KotlinApp.WindowsApp.EmptyMessage(sectionName)
            : string.Empty;

        // Restore list highlight after refresh without re-opening the detail pane.
        if (!ReferenceEquals(_selectedArticle, restoredArticle))
        {
            _selectedArticle = restoredArticle;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(SelectedArticle)));
        }
    }

    private async Task OpenStoryAsync(StorySummaryViewModel story)
    {
        var previous = SelectedStory;
        var disposal = previous?.DisposeAsync().AsTask();
        SelectedStory = new StoryDetailViewModel(story.SectionName, story.Uri, story.Title, _ui);
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

    private Task RunOnUiAsync(Action action)
    {
        if (SynchronizationContext.Current == _ui)
        {
            action();
            return Task.CompletedTask;
        }

        var tcs = new TaskCompletionSource();
        _ui.Post(_ =>
        {
            try
            {
                action();
                tcs.SetResult();
            }
            catch (Exception ex)
            {
                tcs.SetException(ex);
            }
        }, null);
        return tcs.Task;
    }

    private bool SetField<T>(ref T field, T value, [CallerMemberName] string? propertyName = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return false;
        field = value;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        return true;
    }
}
