# Native Windows and macOS hosts

WPF, WinUI 3 and .NET MAUI apps that consume the shared Kotlin domain through a NuGet package
produced by [kotlin-native-nuget](https://github.com/xxfast/kotlin-native-nuget).

## Module chain

```text
:app (domains + state)
  → :app:windows (Kotlin host VMs, this Gradle module) → NYTimes.Kotlin (NuGet)
       ├── Windows.sln
       ├── Shared/     C# view models shared by every .NET host
       ├── WpfApp/
       ├── WinUiApp/
       └── MauiApp/
```

Interop path at runtime:

```text
Shared domain (Molecule) in :app
  → Native .NET host ViewModel (moleculeFlow → StateFlow)          src/nativeMain
  → KotlinStateFlow<T> / IAsyncEnumerable<T> + .Value               generated C#
  → Shared C# view models (NYTimes.Windows)                          Shared/
  → WPF / WinUI / MAUI host UI
```

Shared state and model types (`TopStoriesState`, `StoryState`, `SummaryState`, `Article`,
value classes, `Instant`) export directly from `:app`. `rootPackage = "io.github.xxfast.nytimes"`
in `build.gradle.kts` admits `screens.*` and `models` into the package without local DTOs.

## Build and run

Windows, from the repository root:

```powershell
dotnet build app\windows\Windows.sln -p:Platform=x64

dotnet run --project app\windows\WpfApp\WpfApp.csproj
dotnet run --project app\windows\WinUiApp\WinUiApp.csproj
dotnet run --project app\windows\MauiApp\MauiApp.csproj
```

Apple Silicon macOS (MAUI only):

```bash
dotnet build app/windows/MauiApp/MauiApp.csproj -t:Run -f net10.0-maccatalyst
```

## How the NuGet package is produced

`Directory.Build.targets` and `Directory.Solution.targets` run `:app:windows:packNuget` before
NuGet restore for both project and solution builds. The output lands in `build/nuget`, which
`Directory.Build.props` registers as an additional restore source.

The package version lives once in `nuget.version`; Gradle reads it for `packNuget` and
MSBuild reads it into `$(NYTimesKotlinVersion)` for every `PackageReference` and the cache path
below, so bumping it is a one-line change.

Because the package version is fixed, the restore hook also evicts the repo-local extracted copy
(`obj/packages/nytimes.kotlin/<version>`) and the project assets file so a rebuilt Kotlin binary
is never shadowed by a stale cache.

Pass `-p:SkipNYTimesKotlinNuGetPack=true` to reuse the existing local package without running
Gradle. CI does this after packing once.

## Native runtimes

| Kotlin target | NuGet runtime folder | Consumed by |
|---------------|----------------------|-------------|
| `mingwX64`    | `runtimes/win-x64`   | WPF, WinUI, MAUI on Windows |
| `macosArm64`  | `runtimes/osx-arm64` | MAUI on Mac Catalyst |

.NET's `maccatalyst-arm64` runtime identifier does not fall back to `osx-arm64`, so `MauiApp`
references the macOS dylib as an explicit `NativeReference` in its project file.

## Bugs in the plugin

Defects found in kotlin-native-nuget while building these hosts are tracked on its
[issue tracker](https://github.com/xxfast/kotlin-native-nuget/issues), not in this repository.

## Layouts

WPF and WinUI mirror the Compose window size classes from code-behind (`ApplyLayout`):

| Window width | Layout |
|--------------|--------|
| under 840    | Compact: sections and list, or the detail with a "Stories" button back to the list |
| 840 to 1399  | Expanded: sections, then list and detail split 50/50 |
| 1400 and up  | Wide: the detail widens and related stories move beside the article (60/40) |

## Navigation state

The shared `TopStoriesViewModel` keeps a back stack of opened stories (`GoBackCommand` /
`CanGoBack`) and persists the selected section and open story to
`%LOCALAPPDATA%\NYTimes-KMP\host-state.json`, restoring both on the next launch.

## Diagnostics

The shared C# view models trace flow lifecycle, network failures reported by the domain, and
bridge faults through the `NYTimes.Windows` `TraceSource`, so they show up in the debugger
output window. Each host also appends them to
`%LOCALAPPDATA%\NYTimes-KMP\diagnostics.log`. A faulted flow no longer dies silently: the pane
shows "Lost connection to the shared code" with the exception message.
