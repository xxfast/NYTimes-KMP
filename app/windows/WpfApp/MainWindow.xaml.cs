using System.Windows;
using NYTimes.Windows;
using Wpf.Ui.Appearance;
using Wpf.Ui.Controls;

namespace WpfApp;

public partial class MainWindow : FluentWindow
{
    private readonly TopStoriesViewModel _viewModel;

    public MainWindow()
    {
        InitializeComponent();

        // Follow Windows light/dark theme and keep Mica in sync.
        SystemThemeWatcher.Watch(this);

        // Constructed on the UI thread so SynchronizationContext is captured.
        _viewModel = new TopStoriesViewModel();
        DataContext = _viewModel;
        Closed += async (_, _) => await _viewModel.DisposeAsync();
    }
}
