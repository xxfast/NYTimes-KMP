using Foundation;

namespace NYTimes.MauiApp;

[Register("AppDelegate")]
public class AppDelegate : MauiUIApplicationDelegate
{
    protected override global::Microsoft.Maui.Hosting.MauiApp CreateMauiApp() =>
        MauiProgram.CreateMauiApp();
}
