# Integration Bugs

Active defects from integrating the shared Kotlin domains with `kotlin-native-nuget` and the
desktop .NET hosts (WPF + WinUI 3 + .NET MAUI on Windows/macOS). Missing (but not broken)
functionality is in
[`MISSING-FEATURES.md`](MISSING-FEATURES.md).

## Context

| Component             |                                                                    Version |
|-----------------------|---------------------------------------------------------------------------:|
| Kotlin                |                                                                   `2.4.10` |
| Gradle                |                                                                    `9.7.0` |
| `kotlin-native-nuget` |                                                                    `0.4.0` |
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

### BUG-009: C# reserved keywords are not escaped in generated parameter names

- **Area:** `kotlin-native-nuget` 0.4.0 (still open upstream; tracked in the plugin's
  [`docs/backlog/c-keyword-parameter-names-never-escaped-kotlin.md`](https://github.com/xxfast/kotlin-native-nuget/blob/main/docs/backlog/c-keyword-parameter-names-never-escaped-kotlin.md))
- **Observed:** `Article.abstract` emitted a constructor/P-Invoke parameter literally named
  `abstract`, which does not compile (`@abstract` escaping is missing). Kotlin allows modifier
  keywords as identifiers, so any such property name hits this. The plugin only `@`-escapes
  method names, never parameter names.
- **Workaround:** Renamed the shared property to `description` with `@SerialName("abstract")`,
  keeping the NYT API wire format. Also renamed `published_date` → `publishedDate`
  (`@SerialName`) since snake_case surfaced as `Published_date` in C# (cosmetic, not a compile
  break).

### BUG-010: A Kotlin parameter named `error` collides with the generated exception slot

- **Area:** `kotlin-native-nuget` 0.4.0
- **Observed:** Adding `val error: String?` to `StoryState` / `TopStoriesState` generated
  `Native_Create(..., string? error, out IntPtr error)` and `Native_Copy(...)` P-Invoke
  declarations, which fail with `CS0100: The parameter name 'error' is a duplicate`. The
  generator hard-codes `error` as the name of its `out IntPtr` exception slot (ADR-024 /
  ADR-031) without renaming a user parameter of the same name. Same family as BUG-009
  (identifier collisions are not detected on parameters).
- **Workaround:** The shared state property is `failure`, not `error`; the .NET hosts still
  expose it as `Error` / `HasError`.

## Fixed / retired workarounds

### BUG-002: Network failures have no observable error state

- **Fixed (shared domain):** `TopStoriesState.failure` and `StoryState.failure` (`String?`,
  binds as C# `string?`; named to dodge BUG-010) carry the failure message from the `Result` the
  web service already returned. The domains reset it at the start of every load, so `Refresh`
  doubles as retry. Story detail also reports when a successful fetch no longer contains the
  story instead of loading forever.
- **Hosts:** `IsLoading` is now `articles/article is null && error is null`; the shared VMs
  expose `Error` / `HasError` and WPF, WinUI, and MAUI show the message with a Retry button
  bound to the existing refresh command.
- **Still open:** Compose and Wear screens ignore `failure` and keep spinning (MF-008).

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
