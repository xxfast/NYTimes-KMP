using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using Kotlin = NYTimes.Kotlin;

namespace NYTimes.Windows;

public sealed class StoryDetailViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private readonly SynchronizationContext _ui;
    private readonly CancellationTokenSource _cancellation = new();
    private readonly Kotlin.StoryViewModel _kotlinViewModel;
    private readonly Task _observation;
    private bool _isLoading = true;
    private bool _isSaved;
    private string _articleTitle = string.Empty;
    private string _articleDescription = string.Empty;
    private string _articleSectionName = string.Empty;
    private string _articleByline = string.Empty;
    private string _articleUrl = string.Empty;
    private string _articleImageUrl = string.Empty;
    private int _disposed;

    public StoryDetailViewModel(
        string sectionName,
        string uri,
        string title,
        SynchronizationContext? uiContext = null)
    {
        _ui = uiContext ?? SynchronizationContext.Current
            ?? throw new InvalidOperationException(
                "Create on the UI thread or pass a SynchronizationContext.");
        _kotlinViewModel = new Kotlin.StoryViewModel(sectionName, uri, title);
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

    public string ArticleImageUrl
    {
        get => _articleImageUrl;
        private set
        {
            if (!SetField(ref _articleImageUrl, value)) return;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(HasArticleImage)));
        }
    }

    public bool HasArticleImage => !string.IsNullOrWhiteSpace(ArticleImageUrl);

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
            // Host disposal cancels KotlinFlow collection.
        }
    }

    private void Apply(Kotlin.StoryState state)
    {
        IsLoading = state.Article is null;
        IsSaved = state.HasSavedState && state.IsSaved;

        if (state.Article is null)
        {
            Related.Clear();
            ArticleImageUrl = string.Empty;
            return;
        }

        using var article = state.Article;
        ArticleTitle = article.Title;
        ArticleDescription = article.Description;
        ArticleSectionName = article.SectionName;
        ArticleByline = article.Byline;
        ArticleUrl = article.Url;
        ArticleImageUrl = article.ImageUrl ?? string.Empty;

        Related.Clear();
        foreach (var related in state.Related)
        {
            using (related)
            {
                Related.Add(new StorySummaryViewModel(
                    related.Uri,
                    related.Title,
                    related.Description,
                    related.SectionName,
                    related.Byline,
                    related.ImageUrl ?? string.Empty));
            }
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
