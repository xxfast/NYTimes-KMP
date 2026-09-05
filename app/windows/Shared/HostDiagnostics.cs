using System.Diagnostics;
using System.IO;

namespace NYTimes.Windows;

/// <summary>
/// Structured trace of everything that crosses the Kotlin bridge: flow lifecycle, network
/// failures reported by the shared domain, and bridge faults. Events go to the
/// <c>NYTimes.Windows</c> <see cref="TraceSource"/> (visible in any debugger output window)
/// and, once <see cref="Initialise"/> has run, to <see cref="LogPath"/>.
/// </summary>
public static class HostDiagnostics
{
    public static readonly TraceSource Source = new("NYTimes.Windows", SourceLevels.All);

    /// <summary>Plain-text log next to the shared KStore file, one line per event.</summary>
    public static string LogPath { get; } = Path.Combine(
        Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData),
        "NYTimes-KMP",
        "diagnostics.log");

    private static int _initialised;

    /// <summary>Attaches the file listener once per process; safe to call from every host.</summary>
    public static void Initialise()
    {
        if (Interlocked.Exchange(ref _initialised, 1) != 0) return;

        try
        {
            Directory.CreateDirectory(Path.GetDirectoryName(LogPath)!);
            Source.Listeners.Add(new TextWriterTraceListener(LogPath, "file")
            {
                TraceOutputOptions = TraceOptions.DateTime | TraceOptions.ThreadId,
            });
        }
        catch (Exception exception) when (exception is IOException or UnauthorizedAccessException)
        {
            // Debugger output still works without the file.
            Source.TraceEvent(TraceEventType.Warning, 0, $"[diagnostics] Cannot open {LogPath}: {exception.Message}");
        }
    }

    public static void Info(string area, string message) => Emit(TraceEventType.Information, area, message);

    public static void Warning(string area, string message) => Emit(TraceEventType.Warning, area, message);

    public static void Error(string area, string message, Exception? exception = null) =>
        Emit(TraceEventType.Error, area, exception is null ? message : $"{message}: {exception}");

    private static void Emit(TraceEventType type, string area, string message)
    {
        Source.TraceEvent(type, 0, $"[{area}] {message}");
        Source.Flush();
    }
}
