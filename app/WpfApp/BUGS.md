# Integration Bugs

This file tracks defects encountered while integrating the shared Kotlin presentation layer with
`kotlin-native-nuget` and the WPF host. Missing but not broken functionality is tracked in
[`MISSING-FEATURES.md`](MISSING-FEATURES.md).

## Handoff context for the `kotlin-native-nuget` maintainer

This repository is **NYTimes-KMP**, a Kotlin Multiplatform sample application used to exercise
three libraries maintained by the same author: KStore, Decompose Router, and
`kotlin-native-nuget`. The new WPF application is the first .NET consumer of the shared Kotlin app.
The plugin itself is not developed in this repository; this repository is its consumer and
integration test case.

### Toolchain under test

| Component | Version |
|---|---:|
| Kotlin | `2.4.0` |
| Gradle | `9.4.1` |
| `kotlin-native-nuget` | `0.1.0-alpha01` |
| Kotlin target | `mingwX64` |
| .NET target | `net10.0-windows` / `win-x64` |
| Generated package | `NYTimes.Kotlin` `0.1.0` |

### Relevant repository structure

```text
:app
  Shared networking, persistence, and API models.

:app:presentation
  Shared TopStoriesViewModel and StoryViewModel, their state models, and Molecule domains.
  Targets MinGW but does not apply the NuGet plugin.

:app:windows
  Small MinGW-only export module. Applies kotlin-native-nuget and depends on :app and
  :app:presentation. WindowsApp.kt contains the current compatibility wrappers.

app/WpfApp
  C# WPF consumer of the locally generated NYTimes.Kotlin NuGet package.
```

The intended interop path is:

```text
Kotlin StateFlow
  -> public Flow compatibility property
  -> Windows-local exported state model
  -> generated KotlinFlow<T> / IAsyncEnumerable<T>
  -> WPF await foreach
```

### Build and generated outputs

From the repository root on Windows:

```powershell
.\gradlew.bat :app:windows:compileKotlinMingwX64
.\gradlew.bat :app:windows:packNuget
dotnet restore app\WpfApp\WpfApp.csproj --force --no-cache
dotnet build app\WpfApp\WpfApp.csproj --no-restore
```

Important outputs:

```text
app/windows/build/generated/ksp/mingwX64/mingwX64Main/kotlin/
  io/github/xxfast/kotlin/native/nuget/generated/CNameExports.kt

app/windows/build/generated/ksp/mingwX64/mingwX64Main/resources/Interop.cs

app/windows/build/nuget/NYTimes.Kotlin.0.1.0.nupkg
```

The current branch builds because it contains workarounds. A green build does **not** mean the
plugin defects below are fixed. The highest-priority plugin issues are BUG-003 through BUG-010.
BUG-001, BUG-002, and BUG-011 through BUG-014 belong to the sample application rather than the
plugin.

### Suggested plugin regression coverage

Add focused plugin fixtures for these shapes so fixes do not depend on the full NYTimes sample:

1. A configured export package alongside unrelated public declarations; only configured exports
   should be processed.
2. The `ListInteropProbe` from BUG-005; generated Kotlin must compile and C# must retain generic
   element types and nullability.
3. A `String`-returning function on a Kotlin `object`; C# should expose `string`, not `IntPtr`.
4. Nullable primitive, string, and nested-object properties; C# must preserve absence safely.
5. A value class delegating `CharSequence`; inherited functions must retain their parameters or be
   excluded deliberately.
6. A generic `suspend inline` extension returning `Result<T>`; either generate a valid supported
   surface or emit a clear unsupported-declaration diagnostic instead of invalid Kotlin.
7. `Flow<ExternalDto>` where `ExternalDto` is declared in a dependent KMP module; either generate
   the required exports or reject the surface with a precise diagnostic.

Status meanings:

- **Open**: still affects the application or integration.
- **Worked around**: the defect remains, but the repository avoids the failing path.
- **Resolved locally**: fixed in the sample application; retained here as integration history.

## Open

### BUG-002: Network failures have no observable error state

- **Area:** Shared presentation/runtime
- **Status:** Open
- **Impact:** Both top-stories and story-detail requests can remain in loading state indefinitely.
  Neither state model carries an error, and WPF cannot show a failure message or retry reason.
- **Relationship:** Any future network failure still appears as an endless progress indicator.

## Worked around

### BUG-003: NuGet processing is not scoped by `rootPackage`

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Observed:** Applying the plugin directly to `:app` processed all public declarations. The
  configured `rootPackage` changed the C# namespace but did not filter the declarations being
  processed.
- **Impact:** Unrelated APIs could break generation.
- **Workaround:** Apply the plugin only to the isolated `:app:windows` module.
- **Plugin configuration:** See [`../windows/build.gradle.kts`](../windows/build.gradle.kts).

### BUG-004: Flow element models from dependencies are not emitted to C#

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Observed:** A Windows API returning `Flow<TopStoriesState>` compiled on the Kotlin side, but
  `TopStoriesState` lives in `:app:presentation` and no corresponding usable C# model was generated.
- **Workaround:** Map shared states to Windows-local NuGet DTOs before export.
- **Current wrapper:** See
  [`../windows/src/mingwX64Main/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt`](../windows/src/mingwX64Main/kotlin/io/github/xxfast/nytimes/windows/WindowsApp.kt).

### BUG-005: Some `List<T>` code-generation paths erase the type argument

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Important:** General `List<T>` marshalling is implemented; this is not a lack of list support.
- **Reproduced with:** `List<String>`, `List<LocalDto>`, and nullable `List<LocalDto>?` properties in
  a data class.
- **Minimal source shape:**

  ```kotlin
  data class ListInteropProbeItem(val value: String)

  data class ListInteropProbe(
    val strings: List<String>,
    val items: List<ListInteropProbeItem>,
    val nullableItems: List<ListInteropProbeItem>?,
  )
  ```

- **Generated invalid Kotlin:**

  ```kotlin
  strings: List
  items: List
  nullableItems: List
  val obj: kotlin.collections.List? = source.nullableItems
  ```

- **Compiler error:** `One type argument expected for List<out E>`.
- **Affected paths:** Data-class constructors, generated `copy()` functions, and nullable list
  property getters. Non-null list property getter marshalling is present.
- **Workaround:** Export scalar snapshot flows and expose collections through counts plus indexed
  accessors.

### BUG-006: Projection constructors generate duplicate `IntPtr` constructors

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Observed:** DTO secondary constructors accepting shared state types were represented as
  `IntPtr` in C#. This collided with the generated native-handle constructor. Marking the projection
  constructor `internal` did not prevent the collision.
- **Workaround:** Remove projection constructors and use private top-level mapper functions.
- **Source shape:** A Windows-local data class with a secondary constructor such as
  `constructor(state: TopStoriesState) : this(...)`, where `TopStoriesState` comes from
  `:app:presentation`.

### BUG-007: String-returning object methods can export as raw `IntPtr`

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Observed:** `WindowsApp.sectionName(index)` is generated as returning `IntPtr` instead of a
  marshalled C# `string`.
- **Workaround:** WPF converts the pointer with `Marshal.PtrToStringUTF8`.
- **Source shape:** A `String`-returning function on the exported Kotlin `object WindowsApp` in
  `WindowsApp.kt`.

### BUG-008: Nullable values are not represented consistently in generated C#

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around
- **Observed:**
  - Nullable primitives became non-null C# primitives, losing the distinction between `null` and a
    default value.
  - Nullable nested DTOs surfaced as raw `IntPtr` rather than nullable wrapper objects.
  - Nullable string return paths did not provide a consistently safe nullable C# surface.
- **Workaround:** Use explicit `hasValue` flags, non-null primitive values, empty strings, and scalar
  accessors for nested objects.

### BUG-009: Inherited value-class methods are exported as callable methods without parameters

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around by module isolation
- **Observed:** Processing `ArticleUri : CharSequence` produced wrappers for inherited `get` and
  `subSequence` calls without their required parameters.
- **Impact:** Generated Kotlin did not compile when the plugin was applied directly to `:app`.
- **Declaration:**
  [`../src/commonMain/kotlin/io/github/xxfast/nytimes/models/ApiModels.kt`](../src/commonMain/kotlin/io/github/xxfast/nytimes/models/ApiModels.kt)
  contains `value class ArticleUri(val value: String) : CharSequence by value`.

### BUG-010: Generic suspend extensions generate invalid wrappers

- **Area:** `kotlin-native-nuget`
- **Status:** Worked around by module isolation
- **Observed:** The generic suspend extension `HttpClient.get<T>` generated raw `Function1` and
  `Result` types and failed suspend/type-inference compilation.
- **Impact:** Generated Kotlin did not compile when the plugin was applied directly to `:app`.
- **Declaration:**
  [`../src/commonMain/kotlin/io/github/xxfast/nytimes/utils/HttpClient.kt`](../src/commonMain/kotlin/io/github/xxfast/nytimes/utils/HttpClient.kt)
  contains `suspend inline fun <reified T> HttpClient.get(...): Result<T>`.

## Resolved locally

### BUG-001: CIO TLS sessions are unsupported on Kotlin/Native

- **Area:** WPF/runtime
- **Status:** Resolved locally
- **Observed:** WPF remained at `Loading top stories...` after a request failed.
- **Confirmed cause:** The MinGW `ktor-client-cio` engine throws
  `kotlin.IllegalStateException: TLS sessions are not supported on Native platform`.
- **Resolution:** `mingwX64Main` now uses `ktor-client-winhttp`. CIO remains the engine for Android
  and JVM desktop. With WinHttp, the top-stories flow can leave loading when the request succeeds.
- **Remaining limitation:** BUG-002 still applies: failures are converted to `null` with `getOrNull()`
  and no error state is exposed to the host.

### BUG-011: Local NuGet changes were hidden by the global package cache

- **Area:** Build workflow
- **Status:** Resolved locally
- **Observed:** Repacking `NYTimes.Kotlin` with the unchanged `0.1.0` version left WPF compiling
  against an older cached package.
- **Resolution:** WPF restores packages into `obj/packages`, which is local and ignored.
  Rebuilding the same package version still requires deleting only
  `obj/packages/nytimes.kotlin/0.1.0` before restore. Long-term development package
  versioning is tracked by MF-006.

### BUG-012: Empty detail placeholder could not override the detail template

- **Area:** WPF
- **Status:** Resolved locally
- **Cause:** A locally assigned `ContentTemplate` has higher WPF property precedence than a style
  trigger.
- **Resolution:** Both templates now live in style setters, allowing the `Content == null` trigger
  to select the placeholder.

### BUG-013: Native view-model handles could be disposed before flow collection stopped

- **Area:** WPF/native lifecycle
- **Status:** Resolved locally
- **Resolution:** Cancellation and Kotlin `close()` now happen first, WPF awaits the observation
  task, and only then disposes the generated native handle.

### BUG-014: SwiftUI wrappers did not close the newly host-neutral view models

- **Area:** iOS lifecycle
- **Status:** Resolved locally
- **Cause:** Moving shared view models out of the Decompose-owned base class transferred lifecycle
  responsibility to the host.
- **Resolution:** Both SwiftUI model wrappers call `viewModel.close()` from `deinit`.
