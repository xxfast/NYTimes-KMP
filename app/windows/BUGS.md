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
| `kotlin-native-nuget` |                                                                    `0.3.0` |
| Kotlin targets        |                                                  `mingwX64` / `macosArm64` |
| .NET targets          | `net10.0-windows` / `win-x64`; `net10.0-maccatalyst` / `maccatalyst-arm64` |
| Generated package     |                                                   `NYTimes.Kotlin` `0.2.0` |

```text
:app (domains + state)
  → :app:windows (Kotlin host VMs) → NYTimes.Kotlin
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
  → KotlinStateFlow<T> / IAsyncEnumerable<T> + .Value
  → Shared C# VMs (NYTimes.Windows)
  → WPF / WinUI / MAUI host UI
```

Shared state and model types (`TopStoriesState`, `StoryState`, `SummaryState`, `Article`, value
classes, `Instant`) export directly from `:app` via the ADR-066 reachability closure:
`rootPackage = "io.github.xxfast.nytimes"` admits `screens.*` and `models` without local DTOs.

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

### BUG-009: C# reserved keywords are not escaped in generated parameter names

- **Area:** `kotlin-native-nuget` 0.3.0
- **Observed:** `Article.abstract` emitted a constructor/P-Invoke parameter literally named
  `abstract`, which does not compile (`@abstract` escaping is missing). Kotlin allows modifier
  keywords as identifiers, so any such property name hits this.
- **Workaround:** Renamed the shared property to `description` with `@SerialName("abstract")`,
  keeping the NYT API wire format. Also renamed `published_date` → `publishedDate`
  (`@SerialName`) since snake_case surfaced as `Published_date` in C# (cosmetic, not a compile
  break).

## Fixed / retired workarounds

### BUG-005: Nullable `List<T>?` of object elements hard-failed property generation (plugin 0.3.0)

- **Fixed (ADR-075):** Nullable and mutable collection properties now bind in C#.
  `List<SummaryState>?` no longer aborts KSP with
  `Forward property direct nullable getter is invalid … Collection(kind=LIST, element=ObjectHandle)`.
- **Workaround retired:** `TopStoriesState.articles` and `StoryState.related` are nullable again
  (null = shared `Loading`); the explicit `isLoading` flag and `orEmpty()` mapping are gone.
  C# hosts derive `IsLoading` from `Articles is null`.

### BUG-004: Flow element models from dependencies are not emitted to C# (plugin 0.2.0)

- **Plugin:** ADR-066 reachability closure can admit dependency-module types via `include(...)` /
  `rootPackage` scope.
- **Workaround retired (plugin 0.3.0):** value classes (ADR-077, ADR-079..083) and
  `kotlin.time.Instant` → `DateTimeOffset` (ADR-076) now bind, so the local DTO projection and
  its mappers are deleted; `:app` state/model types export directly (MF-003). `ArticleUri`
  (`CharSequence by value`) and `TopStorySection` bind as C# `readonly record struct`.

### BUG-008: Nullable `Boolean?` is unsupported (plugin 0.2.0)

- **Fixed (ADR-069):** `Boolean?` → `bool?` on constructors, properties, and returns.
- **Sample:** `StoryState.isSaved: Boolean?` (null = unknown / `DontKnowYet`). Presence-pair
  `hasSavedState` / `isSaved` removed. Same for `numberOfFavourites: Int?`.
