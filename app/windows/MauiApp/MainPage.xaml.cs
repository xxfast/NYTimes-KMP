using Microsoft.Maui.ApplicationModel;
using NYTimes.Windows;

namespace NYTimes.MauiApp;

public partial class MainPage : ContentPage
{
    private readonly TopStoriesViewModel _viewModel;
    private Window? _window;

    public MainPage()
    {
        InitializeComponent();

        // Kotlin Flow callbacks can arrive on background threads. The shared ViewModel
        // uses this MAUI context in the same way WPF and WinUI use their UI contexts.
        _viewModel = new TopStoriesViewModel(new MauiSynchronizationContext());
        BindingContext = _viewModel;
        Loaded += OnLoaded;
    }

    private void OnLoaded(object? sender, EventArgs e)
    {
        if (ReferenceEquals(_window, Window)) return;

        if (_window is not null)
            _window.Destroying -= OnWindowDestroying;

        _window = Window;
        if (_window is not null)
            _window.Destroying += OnWindowDestroying;
    }

    private async void OnWindowDestroying(object? sender, EventArgs e)
    {
        Loaded -= OnLoaded;
        if (_window is not null)
            _window.Destroying -= OnWindowDestroying;

        await _viewModel.DisposeAsync();
    }

    private sealed class MauiSynchronizationContext : SynchronizationContext
    {
        public override void Post(SendOrPostCallback callback, object? state) =>
            MainThread.BeginInvokeOnMainThread(() => callback(state));
    }
}
