using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using System.Windows.Input;
using NYTimes.Kotlin.Screens.Story;
using KotlinApp = NYTimes.Kotlin.Windows;

namespace NYTimes.Windows;

public sealed class StoryDetailViewModel : INotifyPropertyChanged, IAsyncDisposable
{
    private readonly SynchronizationContext _ui;
    private readonly CancellationTokenSource _cancellation = new();
    private readonly KotlinApp.StoryViewModel _kotlinViewModel;
    private readonly Task _observation;
    private bool _isLoading = true;
    private string? _errorTitle;
    private string? _error;
    private bool _isSaved;
    private string _articleTitle = string.Empty;
    private string _articleDescription = string.Empty;
    private string _articleSectionName = string.Empty;
    private string _articleByline = string.Empty;
    private string _articleUrl = string.Empty;
    private string _articleImageUrl = string.Empty;
    private string _articleImageCaption = string.Empty;
    private string _articleSubsection = string.Empty;
    private int _disposed;

    /// <param name="openRelated">
    /// Invoked when a related story is chosen; the owner replaces this detail with that story.
    /// </param>
    public StoryDetailViewModel(
        string sectionName,
        string uri,
        string title,
        SynchronizationContext? uiContext = null,
        Action<StorySummaryViewModel>? openRelated = null)
    {
        _ui = uiContext ?? SynchronizationContext.Current
            ?? throw new InvalidOperationException(
                "Create on the UI thread or pass a SynchronizationContext.");
        _kotlinViewModel = new KotlinApp.StoryViewModel(sectionName, uri, title);
        Title = title;
        RefreshCommand = new RelayCommand(_kotlinViewModel.OnRefresh);
        SaveCommand = new RelayCommand(_kotlinViewModel.OnSave);
        OpenRelatedCommand = new RelayCommand<StorySummaryViewModel>(story => openRelated?.Invoke(story));
        _observation = ObserveStatesAsync();
    }

    public string Title { get; }
    public ObservableCollection<StorySummaryViewModel> Related { get; } = [];
    public ICommand RefreshCommand { get; }
    public ICommand SaveCommand { get; }

    /// <summary>Opens a <see cref="Related"/> story in place of this one.</summary>
    public ICommand OpenRelatedCommand { get; }

    public bool IsLoading
    {
        get => _isLoading;
        private set => SetField(ref _isLoading, value);
    }

    /// <summary>Short heading for the failure, e.g. "Story not found"; null unless <see cref="HasError"/>.</summary>
    public string? ErrorTitle
    {
        get => _errorTitle;
        private set => SetField(ref _errorTitle, value);
    }

    /// <summary>Why the last load failed; null while loading or once the article arrives.</summary>
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

    /// <summary>Caption of the hero image; empty when the article has no image or caption.</summary>
    public string ArticleImageCaption
    {
        get => _articleImageCaption;
        private set
        {
            if (!SetField(ref _articleImageCaption, value)) return;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(HasArticleImageCaption)));
        }
    }

    public bool HasArticleImageCaption => !string.IsNullOrWhiteSpace(ArticleImageCaption);

    /// <summary>Second category chip after the section, e.g. "Europe"; empty when none.</summary>
    public string ArticleSubsection
    {
        get => _articleSubsection;
        private set
        {
            if (!SetField(ref _articleSubsection, value)) return;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(HasArticleSubsection)));
        }
    }

    public bool HasArticleSubsection => !string.IsNullOrWhiteSpace(ArticleSubsection);

    public event PropertyChangedEventHandler? PropertyChanged;

    private async Task ObserveStatesAsync()
    {
        HostDiagnostics.Info("flow", $"Story '{Title}': collecting state flow");
        try
        {
            await foreach (var state in _kotlinViewModel.StateFlow.WithCancellation(_cancellation.Token))
            {
                using (state)
                {
                    await RunOnUiAsync(() => Apply(state));
                }
            }
            HostDiagnostics.Info("flow", $"Story '{Title}': state flow completed");
        }
        catch (OperationCanceledException) when (_cancellation.IsCancellationRequested)
        {
            // Host disposal cancels KotlinFlow collection.
            HostDiagnostics.Info("flow", $"Story '{Title}': state flow cancelled by host");
        }
        catch (Exception exception)
        {
            // Without this the observation dies silently and the pane freezes on its last state.
            HostDiagnostics.Error("bridge", $"Story '{Title}': state flow faulted", exception);
            await RunOnUiAsync(() =>
            {
                ErrorTitle = "Lost connection to the shared code";
                Error = exception.Message;
                IsLoading = false;
            });
        }
    }

    private void Apply(StoryState state)
    {
        // null article = shared Loading, unless the domain reported why it never arrived.
        using var failure = state.Failure;
        if (failure is not null && failure.Message != _error)
        {
            HostDiagnostics.Warning(
                "network",
                $"Story '{Title}': {failure.Kind} {failure.StatusCode} {failure.Message}");
        }
        ErrorTitle = failure?.Title;
        Error = failure?.Message;
        IsLoading = state.Article is null && failure is null;
        // null = shared DontKnowYet; only true after save state resolves.
        IsSaved = state.IsSaved == true;

        if (state.Article is null)
        {
            Related.Clear();
            ArticleImageUrl = string.Empty;
            ArticleImageCaption = string.Empty;
            return;
        }

        using var article = state.Article;
        ArticleTitle = article.Title;
        ArticleDescription = article.Description;
        ArticleSectionName = article.Section.Name;
        ArticleSubsection = article.Subsection;
        ArticleByline = article.Byline;
        ArticleUrl = article.Url;

        // First multimedia entry drives the detail image, matching the Compose hosts.
        var imageUrl = string.Empty;
        var imageCaption = string.Empty;
        foreach (var media in article.Multimedia ?? [])
        {
            using (media)
            {
                if (imageUrl.Length == 0)
                {
                    imageUrl = media.Url;
                    imageCaption = media.Caption;
                }
            }
        }
        ArticleImageUrl = imageUrl;
        ArticleImageCaption = imageCaption;

        Related.Clear();
        // null related = shared Loading.
        foreach (var related in state.Related ?? [])
        {
            using (related)
            {
                Related.Add(new StorySummaryViewModel(
                    related.Uri.Value,
                    related.Title,
                    related.Description,
                    related.Section.Name,
                    related.Byline,
                    related.ImageUrl ?? string.Empty));
            }
        }
    }

    public async ValueTask DisposeAsync()
    {
        if (Interlocked.Exchange(ref _disposed, 1) != 0) return;

        HostDiagnostics.Info("bridge", $"Story '{Title}': disposing");
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
