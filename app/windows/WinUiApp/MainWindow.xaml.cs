using System.ComponentModel;
using Microsoft.UI;
using Microsoft.UI.Composition.SystemBackdrops;
using Microsoft.UI.Dispatching;
using Microsoft.UI.Windowing;
using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Controls;
using Microsoft.UI.Xaml.Media;
using Microsoft.UI.Xaml.Media.Imaging;
using NYTimes.Windows;
using Windows.Graphics;
using WinRT.Interop;

namespace WinUiApp;

public sealed partial class MainWindow : Window
{
    private readonly TopStoriesViewModel _viewModel;
    private StoryDetailViewModel? _detail;
    private readonly AppWindow _appWindow;

    public MainWindow()
    {
        InitializeComponent();

        // System Mica + custom title bar (Windows 11 look).
        if (MicaController.IsSupported())
        {
            SystemBackdrop = new MicaBackdrop { Kind = MicaKind.Base };
        }
        ExtendsContentIntoTitleBar = true;
        SetTitleBar(AppTitleBar);

        _appWindow = GetAppWindow();
        _appWindow.Title = "The New York Times";
        _appWindow.Resize(new SizeInt32(1280, 800));
        TryCenterOnScreen();

        // Keep custom content clear of min/max/close (and left inset on RTL / tablet).
        _appWindow.Changed += (_, args) =>
        {
            if (args.DidPresenterChange || args.DidSizeChange)
                ApplyTitleBarInsets();
        };
        Activated += (_, _) => ApplyTitleBarInsets();
        ApplyTitleBarInsets();

        // Shared VMs marshal flow updates via SynchronizationContext.
        SynchronizationContext.SetSynchronizationContext(
            new DispatcherQueueSynchronizationContext(DispatcherQueue));

        _viewModel = new TopStoriesViewModel();
        SectionsList.ItemsSource = _viewModel.Sections;
        ArticlesList.ItemsSource = _viewModel.Articles;
        _viewModel.PropertyChanged += ViewModelOnPropertyChanged;
        ApplyLoading();

        Closed += async (_, _) =>
        {
            _viewModel.PropertyChanged -= ViewModelOnPropertyChanged;
            if (_detail is not null)
            {
                _detail.PropertyChanged -= DetailOnPropertyChanged;
                await _detail.DisposeAsync();
            }
            await _viewModel.DisposeAsync();
        };
    }

    private AppWindow GetAppWindow()
    {
        var hwnd = WindowNative.GetWindowHandle(this);
        var id = Win32Interop.GetWindowIdFromWindow(hwnd);
        return AppWindow.GetFromWindowId(id);
    }

    private void TryCenterOnScreen()
    {
        try
        {
            var display = DisplayArea.GetFromWindowId(_appWindow.Id, DisplayAreaFallback.Nearest);
            var work = display.WorkArea;
            var x = work.X + (work.Width - _appWindow.Size.Width) / 2;
            var y = work.Y + (work.Height - _appWindow.Size.Height) / 2;
            _appWindow.Move(new PointInt32(x, y));
        }
        catch
        {
            // Non-fatal if display metrics are unavailable.
        }
    }

    /// <summary>
    /// Reserve the system caption-button strip so Refresh (and title content) never draw under Close.
    /// Insets are in physical pixels; convert to XAML DIPs via rasterization scale.
    /// </summary>
    private void ApplyTitleBarInsets()
    {
        try
        {
            var titleBar = _appWindow.TitleBar;
            var scale = Content is FrameworkElement fe && fe.XamlRoot is { } root
                ? root.RasterizationScale
                : 1.0;

            var leftDip = titleBar.LeftInset / scale;
            var rightDip = titleBar.RightInset / scale;

            // Fallback if the platform reports 0 before the chrome is ready (~138px at 100% scale).
            if (rightDip < 1)
                rightDip = 138;

            TitleBarLeftPaddingColumn.Width = new GridLength(leftDip);
            TitleBarRightPaddingColumn.Width = new GridLength(rightDip);
        }
        catch
        {
            TitleBarRightPaddingColumn.Width = new GridLength(138);
        }
    }

    private void ViewModelOnPropertyChanged(object? sender, PropertyChangedEventArgs e)
    {
        if (e.PropertyName is nameof(TopStoriesViewModel.IsLoading)
            or nameof(TopStoriesViewModel.Error)
            or null)
            ApplyLoading();

        if (e.PropertyName is nameof(TopStoriesViewModel.SelectedStory) or null)
            BindDetail(_viewModel.SelectedStory);

        if (e.PropertyName is nameof(TopStoriesViewModel.SelectedSection) or null &&
            !ReferenceEquals(SectionsList.SelectedItem, _viewModel.SelectedSection))
            SectionsList.SelectedItem = _viewModel.SelectedSection;

        if (e.PropertyName is nameof(TopStoriesViewModel.SelectedArticle) or null &&
            !ReferenceEquals(ArticlesList.SelectedItem, _viewModel.SelectedArticle))
            ArticlesList.SelectedItem = _viewModel.SelectedArticle;
    }

    private void ApplyLoading()
    {
        LoadingRing.IsActive = _viewModel.IsLoading;
        LoadingRing.Visibility = _viewModel.IsLoading ? Visibility.Visible : Visibility.Collapsed;
        ErrorText.Text = _viewModel.Error ?? string.Empty;
        ErrorPanel.Visibility = _viewModel.HasError ? Visibility.Visible : Visibility.Collapsed;
    }

    private void BindDetail(StoryDetailViewModel? detail)
    {
        if (_detail is not null)
            _detail.PropertyChanged -= DetailOnPropertyChanged;

        _detail = detail;
        if (_detail is null)
        {
            EmptyDetail.Visibility = Visibility.Visible;
            DetailContent.Visibility = Visibility.Collapsed;
            RelatedList.ItemsSource = null;
            HeroImage.Source = null;
            HeroImageBorder.Visibility = Visibility.Collapsed;
            return;
        }

        EmptyDetail.Visibility = Visibility.Collapsed;
        DetailContent.Visibility = Visibility.Visible;
        RelatedList.ItemsSource = _detail.Related;
        _detail.PropertyChanged += DetailOnPropertyChanged;
        ApplyDetail();
    }

    private void DetailOnPropertyChanged(object? sender, PropertyChangedEventArgs e) => ApplyDetail();

    private void ApplyDetail()
    {
        if (_detail is null) return;

        ArticleTitle.Text = _detail.ArticleTitle;
        ArticleByline.Text = _detail.ArticleByline;
        ArticleSection.Text = string.IsNullOrWhiteSpace(_detail.ArticleSectionName)
            ? string.Empty
            : _detail.ArticleSectionName.ToUpperInvariant();
        ArticleSection.Visibility = string.IsNullOrWhiteSpace(_detail.ArticleSectionName)
            ? Visibility.Collapsed
            : Visibility.Visible;
        ArticleDescription.Text = _detail.ArticleDescription;

        if (_detail.HasArticleImage &&
            Uri.TryCreate(_detail.ArticleImageUrl, UriKind.Absolute, out var imageUri))
        {
            HeroImage.Source = new BitmapImage(imageUri);
            HeroImageBorder.Visibility = Visibility.Visible;
        }
        else
        {
            HeroImage.Source = null;
            HeroImageBorder.Visibility = Visibility.Collapsed;
        }

        DetailLoadingRing.IsActive = _detail.IsLoading;
        DetailLoadingRing.Visibility = _detail.IsLoading ? Visibility.Visible : Visibility.Collapsed;
        DetailErrorText.Text = _detail.Error ?? string.Empty;
        DetailErrorPanel.Visibility = _detail.HasError ? Visibility.Visible : Visibility.Collapsed;

        if (_detail.IsSaved)
        {
            SaveIcon.Glyph = "\uE735"; // Solid star
            SaveButtonText.Text = "Saved";
        }
        else
        {
            SaveIcon.Glyph = "\uE734"; // Outline star
            SaveButtonText.Text = "Save";
        }
    }

    private void RefreshButton_Click(object sender, RoutedEventArgs e) =>
        _viewModel.RefreshCommand.Execute(null);

    private void SectionsList_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (SectionsList.SelectedItem is SectionViewModel section)
            _viewModel.SelectedSection = section;
    }

    private void ArticlesList_SelectionChanged(object sender, SelectionChangedEventArgs e)
    {
        if (ArticlesList.SelectedItem is StorySummaryViewModel article)
            _viewModel.SelectedArticle = article;
    }

    private void SaveButton_Click(object sender, RoutedEventArgs e) =>
        _detail?.SaveCommand.Execute(null);

    private void ReloadButton_Click(object sender, RoutedEventArgs e) =>
        _detail?.RefreshCommand.Execute(null);
}
