# Integration Bugs

Active defects from integrating the shared Kotlin domains with `kotlin-native-nuget` and the
desktop .NET hosts (WPF + WinUI 3 + .NET MAUI on Windows/macOS). Missing (but not broken)
functionality is in
[`MISSING-FEATURES.md`](MISSING-FEATURES.md).

## Context

| Component             |                                                                    Version |
|-----------------------|---------------------------------------------------------------------------:|
| Kotlin                |                                                                    `2.4.0` |
| Gradle                |                                                                    `9.4.1` |
| `kotlin-native-nuget` |                                                            `0.1.0-alpha02` |
| Kotlin targets        |                                                  `mingwX64` / `macosArm64` |
| .NET targets          | `net10.0-windows` / `win-x64`; `net10.0-maccatalyst` / `maccatalyst-arm64` |
| Generated package     |                                                   `NYTimes.Kotlin` `0.1.0` |

```text
:app (domains + state)
  → :app:windows (Kotlin host VMs + NuGet DTOs) → NYTimes.Kotlin
       ├── Windows.sln
       ├── Shared/          (C# VMs shared by all .NET hosts)
       ├── WpfApp/
       ├── WinUiApp/
       └── MauiApp/
:app → :app:compose (Compose / iOS host VMs + UI)
```

Interop path today:

```text
Shared domain (Molecule) in :app
  → Native .NET host ViewModel (moleculeFlow)
  → NuGet DTO projection
  → KotlinFlow<T> / IAsyncEnumerable<T>
  → Shared C# VMs (NYTimes.Windows)
  → WPF / WinUI / MAUI host UI
```

Build (Windows, from repo root):

```powershell
dotnet build app\windows\Windows.sln -p:Platform=x64
# Hosts:
#   dotnet run --project app\windows\WpfApp\WpfApp.csproj
#   dotnet run --project app\windows\WinUiApp\WinUiApp.csproj
#   dotnet run --project app\windows\MauiApp\MauiApp.csproj
```

Build and run on Apple Silicon macOS:

```bash
dotnet build app/windows/MauiApp/MauiApp.csproj -t:Run -f net10.0-maccatalyst
```

The shared MSBuild targets run `:app:windows:packNuget` before NuGet restore and refresh the
repo-local extracted package automatically for project and solution builds. Pass
`-p:SkipNYTimesKotlinNuGetPack=true` only when the existing local package should be reused
without running Gradle.

The NuGet plugin publishes `macosArm64` under `runtimes/osx-arm64`. `MauiApp` includes that
dylib as an explicit `NativeReference` because .NET's `maccatalyst-arm64` RID does not fall back
to `osx-arm64`.

## Open

### BUG-002: Network failures have no observable error state

- **Area:** Shared domain / runtime
- **Impact:** Top-stories and story-detail can stay in loading forever. Neither state model
  carries an error, so the .NET hosts cannot show a failure message or retry reason.

## Worked around (plugin)

### BUG-004: Flow element models from dependencies are not emitted to C#

- **Area:** `kotlin-native-nuget`
- **Observed:** Domain state types live in `:app` and are outside the export set
  (`rootPackage = io.github.xxfast.nytimes.windows`). Only local DTOs are bridged.
- **Workaround:** Project shared state to NuGet DTOs in
  [`src/nativeMain/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt`](src/nativeMain/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt).

### BUG-005: Nullable `List<T>?` property getters fail generation

- **Area:** `kotlin-native-nuget`
- **Fixed already:** Non-null `List<T>` → `IReadOnlyList<T>` with element types preserved.
- **Still broken:** Nullable `List<T>?` getters throw during KSP.
- **Workaround:** Non-null lists (`orEmpty()`) plus an explicit loading flag.

### BUG-008: Nullable `Boolean?` is unsupported

- **Area:** `kotlin-native-nuget`
- **Fixed already:** `String?` and nullable nested local DTOs.
- **Still broken:** `Boolean?` on data-class constructors is skipped.
- **Workaround:** `hasSavedState` / `isSaved` (and similar) presence pairs.
