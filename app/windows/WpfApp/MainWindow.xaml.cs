using System.ComponentModel;
using System.Windows;
using NYTimes.Windows;
using Wpf.Ui.Appearance;
using Wpf.Ui.Controls;

namespace WpfApp;

public partial class MainWindow : FluentWindow
{
    /// <summary>Below this width the list and the detail take turns filling the window.</summary>
    private const double CompactBreakpoint = 840;

    /// <summary>From this width the detail pane splits article and related stories 60/40.</summary>
    private const double WideBreakpoint = 1400;

    public static readonly DependencyProperty IsCompactProperty = DependencyProperty.Register(
        nameof(IsCompact), typeof(bool), typeof(MainWindow), new PropertyMetadata(false));

    public static readonly DependencyProperty IsWideProperty = DependencyProperty.Register(
        nameof(IsWide), typeof(bool), typeof(MainWindow), new PropertyMetadata(false));

    private readonly TopStoriesViewModel _viewModel;

    public MainWindow()
    {
        InitializeComponent();

        // Follow Windows light/dark theme and keep Mica in sync.
        SystemThemeWatcher.Watch(this);

        // Constructed on the UI thread so SynchronizationContext is captured.
        _viewModel = new TopStoriesViewModel();
        DataContext = _viewModel;
        _viewModel.PropertyChanged += OnViewModelPropertyChanged;
        SizeChanged += (_, _) => ApplyLayout();
        Loaded += (_, _) => ApplyLayout();
        Closed += async (_, _) =>
        {
            _viewModel.PropertyChanged -= OnViewModelPropertyChanged;
            await _viewModel.DisposeAsync();
        };
    }

    /// <summary>True when the window is narrower than <see cref="CompactBreakpoint"/>.</summary>
    public bool IsCompact
    {
        get => (bool)GetValue(IsCompactProperty);
        private set => SetValue(IsCompactProperty, value);
    }

    /// <summary>True when the window is at least <see cref="WideBreakpoint"/> wide.</summary>
    public bool IsWide
    {
        get => (bool)GetValue(IsWideProperty);
        private set => SetValue(IsWideProperty, value);
    }

    private void OnViewModelPropertyChanged(object? sender, PropertyChangedEventArgs e)
    {
        if (e.PropertyName is nameof(TopStoriesViewModel.HasSelectedStory) or null)
            ApplyLayout();
    }

    /// <summary>
    /// Mirrors the Compose layouts: compact shows the list or the detail, expanded splits
    /// list and detail 50/50, and wide widens the detail so it can hold related stories beside
    /// the article.
    /// </summary>
    private void ApplyLayout()
    {
        var width = ActualWidth;
        IsCompact = width < CompactBreakpoint;
        IsWide = width >= WideBreakpoint;

        if (IsCompact)
        {
            var showDetail = _viewModel.HasSelectedStory;
            SectionsPane.Visibility = showDetail ? Visibility.Collapsed : Visibility.Visible;
            StoriesPane.Visibility = showDetail ? Visibility.Collapsed : Visibility.Visible;
            DetailPane.Visibility = showDetail ? Visibility.Visible : Visibility.Collapsed;
            SectionsColumn.Width = showDetail ? new GridLength(0) : new GridLength(180);
            StoriesColumn.Width = showDetail ? new GridLength(0) : new GridLength(1, GridUnitType.Star);
            DetailColumn.Width = showDetail ? new GridLength(1, GridUnitType.Star) : new GridLength(0);
            return;
        }

        SectionsPane.Visibility = Visibility.Visible;
        StoriesPane.Visibility = Visibility.Visible;
        DetailPane.Visibility = Visibility.Visible;
        SectionsColumn.Width = new GridLength(220);
        StoriesColumn.Width = new GridLength(1, GridUnitType.Star);
        DetailColumn.Width = IsWide
            ? new GridLength(1.5, GridUnitType.Star)
            : new GridLength(1, GridUnitType.Star);
    }
}
