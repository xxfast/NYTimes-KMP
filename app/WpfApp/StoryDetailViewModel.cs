using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using System.Windows.Threading;
using NYTimes.Kotlin;

namespace WpfApp;

public sealed class StoryDetailViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private readonly Dispatcher _dispatcher;
    private readonly CancellationTokenSource _cancellation = new();
    private readonly WindowsStoryViewModel _kotlinViewModel;
    private readonly Task _observation;
    private bool _isLoading = true;
    private bool _isSaved;
    private string _articleTitle = string.Empty;
    private string _articleDescription = string.Empty;
    private string _articleSectionName = string.Empty;
    private string _articleByline = string.Empty;
    private string _articleUrl = string.Empty;
    private int _disposed;

    public StoryDetailViewModel(Dispatcher dispatcher, string sectionName, string uri, string title)
    {
        _dispatcher = dispatcher;
        _kotlinViewModel = new WindowsStoryViewModel(sectionName, uri, title);
        Title = title;
        RefreshCommand = new RelayCommand(_kotlinViewModel.OnRefresh);
        SaveCommand = new RelayCommand(_kotlinViewModel.OnSave);
        _observation = ObserveStatesAsync();
    }

    public string Title { get; }
    public ObservableCollection<StorySummaryViewModel> Related { get; } = [];
    public ICommand RefreshCommand { get; }
    public ICommand SaveCommand { get; }

    public bool IsLoading
    {
        get => _isLoading;
        private set => SetField(ref _isLoading, value);
    }

    public bool IsSaved
    {
        get => _isSaved;
        private set => SetField(ref _isSaved, value);
    }

    public string ArticleTitle
    {
        get => _articleTitle;
        private set => SetField(ref _articleTitle, value);
    }

    public string ArticleDescription
    {
        get => _articleDescription;
        private set => SetField(ref _articleDescription, value);
    }

    public string ArticleSectionName
    {
        get => _articleSectionName;
        private set => SetField(ref _articleSectionName, value);
    }

    public string ArticleByline
    {
        get => _articleByline;
        private set => SetField(ref _articleByline, value);
    }

    public string ArticleUrl
    {
        get => _articleUrl;
        private set => SetField(ref _articleUrl, value);
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
            // Replacing the detail pane or closing the window stops KotlinFlow collection.
        }
    }

    private void Apply(WindowsStoryState state)
    {
        IsLoading = !state.HasArticle;
        IsSaved = state.HasSavedState && state.IsSaved;

        if (!state.HasArticle)
        {
            Related.Clear();
            return;
        }

        ArticleTitle = _kotlinViewModel.ArticleTitle();
        ArticleDescription = _kotlinViewModel.ArticleDescription();
        ArticleSectionName = _kotlinViewModel.ArticleSectionName();
        ArticleByline = _kotlinViewModel.ArticleByline();
        ArticleUrl = _kotlinViewModel.ArticleUrl();

        Related.Clear();
        for (var index = 0; index < state.RelatedCount; index++)
        {
            Related.Add(new StorySummaryViewModel(
                _kotlinViewModel.RelatedUri(index),
                _kotlinViewModel.RelatedTitle(index),
                _kotlinViewModel.RelatedDescription(index),
                _kotlinViewModel.RelatedSectionName(index),
                _kotlinViewModel.RelatedByline(index),
                _kotlinViewModel.RelatedImageUrl(index)));
        }
    }

    public async ValueTask DisposeAsync()
    {
        if (Interlocked.Exchange(ref _disposed, 1) != 0) return;

        _cancellation.Cancel();
        _kotlinViewModel.Close();
        try { await _observation; }
        catch (OperationCanceledException) { }
        finally
        {
            await _kotlinViewModel.DisposeAsync();
            _cancellation.Dispose();
        }
    }

    private void SetField<T>(ref T field, T value, [CallerMemberName] string? propertyName = null)
    {
        if (EqualityComparer<T>.Default.Equals(field, value)) return;
        field = value;
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
    }
}
