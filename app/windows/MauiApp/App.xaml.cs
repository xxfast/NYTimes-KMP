namespace NYTimes.MauiApp;

public partial class App : Application
{
    public App()
    {
        InitializeComponent();
    }

    protected override Window CreateWindow(IActivationState? activationState) =>
        new(new AppShell())
        {
            Title = "The New York Times",
            Width = 1280,
            Height = 800,
            MinimumWidth = 900,
            MinimumHeight = 600
        };
}
