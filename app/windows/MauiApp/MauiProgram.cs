using Microsoft.Extensions.Logging;

namespace NYTimes.MauiApp;

public static class MauiProgram
{
    public static global::Microsoft.Maui.Hosting.MauiApp CreateMauiApp()
    {
        var builder = global::Microsoft.Maui.Hosting.MauiApp.CreateBuilder();

        builder.UseMauiApp<App>();

#if DEBUG
        builder.Logging.AddDebug();
#endif

        return builder.Build();
    }
}
