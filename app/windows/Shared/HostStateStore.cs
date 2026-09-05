using System.IO;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace NYTimes.Windows;

/// <summary>What the hosts restore after a process restart: the section and the open story.</summary>
public sealed record HostState(
    string? Section,
    string? StoryUri,
    string? StorySection,
    string? StoryTitle);

[JsonSerializable(typeof(HostState))]
internal sealed partial class HostStateJsonContext : JsonSerializerContext;

/// <summary>
/// Persists <see cref="HostState"/> as JSON next to the shared KStore file. Failures are logged
/// through <see cref="HostDiagnostics"/> and otherwise ignored; restore is best effort.
/// </summary>
public sealed class HostStateStore(string directory)
{
    private readonly string _path = Path.Combine(directory, "host-state.json");

    public HostState? Load()
    {
        try
        {
            if (!File.Exists(_path)) return null;
            return JsonSerializer.Deserialize(File.ReadAllText(_path), HostStateJsonContext.Default.HostState);
        }
        catch (Exception exception) when (exception is IOException or JsonException or UnauthorizedAccessException)
        {
            HostDiagnostics.Warning("restore", $"Ignoring unreadable host state: {exception.Message}");
            return null;
        }
    }

    public void Save(HostState state)
    {
        try
        {
            File.WriteAllText(_path, JsonSerializer.Serialize(state, HostStateJsonContext.Default.HostState));
        }
        catch (Exception exception) when (exception is IOException or UnauthorizedAccessException)
        {
            HostDiagnostics.Warning("restore", $"Could not save host state: {exception.Message}");
        }
    }
}
