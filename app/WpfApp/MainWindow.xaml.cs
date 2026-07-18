using System.Windows;
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

        _viewModel = new TopStoriesViewModel(Dispatcher);
        DataContext = _viewModel;
        Closed += async (_, _) => await _viewModel.DisposeAsync();
    }
}
