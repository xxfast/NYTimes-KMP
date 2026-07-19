# Integration Bugs

Active defects from integrating the shared Kotlin domains with `kotlin-native-nuget` and the
Windows .NET hosts (WPF + WinUI 3). Missing (but not broken) functionality is in
[`MISSING-FEATURES.md`](MISSING-FEATURES.md).

## Context

| Component | Version |
|---|---:|
| Kotlin | `2.4.0` |
| Gradle | `9.4.1` |
| `kotlin-native-nuget` | `0.1.0-alpha02` |
| Kotlin target | `mingwX64` |
| .NET target | `net10.0-windows` / `win-x64` |
| Generated package | `NYTimes.Kotlin` `0.1.0` |

```text
:app (domains + state)
  → :app:windows (Kotlin host VMs + NuGet DTOs) → NYTimes.Kotlin
       ├── Windows.sln
       ├── Shared/          (C# VMs shared by all .NET hosts)
       ├── WpfApp/
       └── WinUiApp/
:app → :app:compose (Compose / iOS host VMs + UI)
```

Interop path today:

```text
Shared domain (Molecule) in :app
  → Windows host ViewModel (moleculeFlow)
  → NuGet DTO projection
  → KotlinFlow<T> / IAsyncEnumerable<T>
  → Shared C# VMs (NYTimes.Windows)
  → WPF / WinUI host UI
```

Build (Windows, from repo root):

```powershell
.\gradlew.bat :app:windows:packNuget
Remove-Item -Recurse -Force app\windows\*\obj\packages\nytimes.kotlin -ErrorAction SilentlyContinue
dotnet restore app\windows\Windows.sln --force --no-cache
dotnet build app\windows\Windows.sln -p:Platform=x64 --no-restore
# Hosts:
#   dotnet run --project app\windows\WpfApp\WpfApp.csproj
#   dotnet run --project app\windows\WinUiApp\WinUiApp.csproj -p:Platform=x64
```

## Open

### BUG-002: Network failures have no observable error state

- **Area:** Shared domain / runtime
- **Impact:** Top-stories and story-detail can stay in loading forever. Neither state model
  carries an error, so WPF and WinUI cannot show a failure message or retry reason.

## Worked around (plugin)

### BUG-004: Flow element models from dependencies are not emitted to C#

- **Area:** `kotlin-native-nuget`
- **Observed:** Domain state types live in `:app` and are outside the export set
  (`rootPackage = io.github.xxfast.nytimes.windows`). Only local DTOs are bridged.
- **Workaround:** Project shared state to NuGet DTOs in
  [`src/mingwX64Main/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt`](src/mingwX64Main/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt).

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
