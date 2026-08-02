# Integration Bugs

Active defects from integrating the shared Kotlin domains with `kotlin-native-nuget` and the
desktop .NET hosts (WPF + WinUI 3 + .NET MAUI on Windows/macOS). Missing (but not broken)
functionality is in
[`MISSING-FEATURES.md`](MISSING-FEATURES.md).

## Context

| Component             |                                                                    Version |
|-----------------------|---------------------------------------------------------------------------:|
| Kotlin                |                                                                   `2.4.10` |
| Gradle                |                                                                    `9.4.1` |
| `kotlin-native-nuget` |                                                                    `0.2.0` |
| Kotlin targets        |                                                  `mingwX64` / `macosArm64` |
| .NET targets          | `net10.0-windows` / `win-x64`; `net10.0-maccatalyst` / `maccatalyst-arm64` |
| Generated package     |                                                   `NYTimes.Kotlin` `0.2.0` |

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
  → Native .NET host ViewModel (moleculeFlow → StateFlow)
  → NuGet DTO projection
  → KotlinStateFlow<T> / IAsyncEnumerable<T> + .Value
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

### BUG-005: Nullable `List<T>?` of object elements still hard-fails property generation

- **Area:** `kotlin-native-nuget` 0.2.0
- **Observed:** `List<SummaryState>?` on a data-class property aborts KSP with
  `Forward property direct nullable getter is invalid … Collection(kind=LIST, element=ObjectHandle)`.
- **Workaround:** Non-null `List<T>` (`orEmpty()`) plus an explicit `isLoading` flag on top-stories
  state. Related lists on story state also use `orEmpty()`.
- **Note:** Non-null `List<T>` and nullable scalar/object properties work. Bug is specific to
  nullable collection *properties* of object element type (at least).

## Fixed / retired workarounds (plugin 0.2.0)

### BUG-004: Flow element models from dependencies are not emitted to C#

- **Plugin:** ADR-066 reachability closure can admit dependency-module types via `include(...)` /
  `rootPackage` scope.
- **Sample still projects DTOs:** Shared models pull value classes (`ArticleUri` with
  `CharSequence by value`), `Instant`, etc. Local DTOs stay intentional until those shapes are a
  clean export surface (see MF-003).

### BUG-008: Nullable `Boolean?` is unsupported

- **Fixed (ADR-069):** `Boolean?` → `bool?` on constructors, properties, and returns.
- **Sample:** `StoryState.isSaved: Boolean?` (null = unknown / `DontKnowYet`). Presence-pair
  `hasSavedState` / `isSaved` removed. Same for `numberOfFavourites: Int?`.
