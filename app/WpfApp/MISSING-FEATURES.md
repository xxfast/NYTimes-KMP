# Missing Features

This file tracks capabilities found to be absent or incomplete during the WPF and
`kotlin-native-nuget` integration. Confirmed broken behavior is tracked in [`BUGS.md`](BUGS.md).

## Handoff context and ownership

NYTimes-KMP is a Kotlin Multiplatform sample application. It now packages a MinGW shared library as
the local NuGet package `NYTimes.Kotlin` and consumes it from a .NET 10 WPF application. The module
chain is:

```text
:app -> :app:presentation -> :app:windows -> NYTimes.Kotlin -> app/WpfApp
```

The exact toolchain, module responsibilities, build commands, generated-file locations, and
current interop pipeline are documented in the
[`BUGS.md` handoff section](BUGS.md#handoff-context-for-the-kotlin-native-nuget-maintainer).

Ownership for this handoff:

| Items | Owner / repository |
|---|---|
| MF-001 to MF-003 | `kotlin-native-nuget` plugin |
| MF-004 | Decompose Router |
| MF-005 to MF-024 | NYTimes-KMP sample/build/UI |

For the plugin handoff, prioritize MF-001 through MF-003 together with BUG-003 through BUG-010.
The remaining entries provide integration context but should not be treated as plugin defects.

## Kotlin and NuGet integration

### MF-001: Declaration-level export scoping

The NuGet plugin needs an include/exclude mechanism, annotation, or effective package filter so it
can export only the intended public API. Until then, `:app:windows` must remain isolated from
unrelated public declarations.

### MF-002: Direct `StateFlow<T>` export

The shared view models currently expose `Flow<T>` for Windows. Restore their NuGet-facing API to
`StateFlow<T>` when the plugin supports it directly, including current-value access and collection.

### MF-003: Transitive model export

A flow or method should be able to expose a model declared in a dependency module and have the
required C# wrapper and native exports generated automatically. Windows-local state projections are
currently required.

### MF-004: MinGW support in Decompose Router

Decompose Router has no MinGW artifact. Shared presentation view models therefore cannot depend on
`RouterContext`; Compose/iOS provide restored initial state through route adapters while WPF owns
its lifecycle directly.

### MF-005: Automated NuGet production and restore

The WPF build does not automatically run `:app:windows:packNuget`. Developers must pack first and
then restore/build WPF. This should become a reliable build task or script with correct incremental
inputs.

### MF-006: Local development package versioning

The local package remains at `0.1.0`. A development version or generated version should prevent
stale-cache ambiguity without requiring a project-local package cache workaround.

### MF-007: Additional Windows architectures

The package currently targets only `win-x64`. ARM64 and any required x86 support are not packaged or
tested.

## WPF runtime behavior

### MF-008: Explicit loading, error, empty, and retry states

Top-stories and story-detail state models need explicit failure information. WPF needs corresponding
error and empty views plus a retry action. This is required to resolve the user-visible behavior in
BUG-001 and BUG-002.

### MF-009: Verified end-to-end data loading

Builds and launch smoke tests pass, but successful API loading, section changes, article selection,
save/unsave, and related-story loading have not yet been verified together in the running WPF app.

### MF-010: WPF navigation and state restoration

WPF currently owns a selected detail pane but has no navigation history, compact back navigation, or
restoration of the selected section/article across process recreation.

### MF-011: Runtime diagnostics

Flow, networking, and native bridge failures are not surfaced through structured logs or a visible
diagnostic state. Failures can currently appear only as an endless loading indicator or a faulted
background task.

## Compose design parity

### MF-012: Article images

Top-story cards and story detail do not display remote article images. WPF still needs asynchronous
loading, sizing, cropping, caching, progress, and error placeholders.

### MF-013: Responsive compact and expanded layouts

The current WPF window uses a fixed list/detail split. It still needs the Compose behavior:

- Compact width: full-screen list-to-detail navigation with Back.
- Expanded width: 50/50 top-stories list/detail panes.
- Wide detail: 60/40 article/related-story panes.

### MF-014: Masthead, attribution, and icon parity

The current masthead is text and actions use text buttons. Compose vector paths still need to become
WPF `DrawingImage` resources for the masthead, NYT attribution, refresh, back, favourite, fullscreen,
external-link, and error icons.

### MF-015: Theme and typography resources

Colors, typography, spacing, corner radii, and control styles are currently embedded in
`MainWindow.xaml`. They need reusable theme dictionaries matching the Compose Material presentation.

### MF-016: Top-story card parity

Cards still need images, adaptive minimum width, selected state, exact typography/spacing, favourite
count presentation, and the NYT attribution footer.

### MF-017: Story-detail parity

Story detail still needs a hero image, Back, fullscreen/side-panel controls, a stateful favourite
icon, clickable external URL, categories, image metadata, and closer Compose spacing and typography.

### MF-018: Related-story navigation

Related stories are displayed but cannot be selected to replace or navigate the current detail.

### MF-019: Favourite-section parity

The section exists and saving is wired, but WPF still needs the Compose favourite count label,
loading behavior, saved/unsaved presentation, and full favourite-list validation.

## Quality and verification

### MF-020: View-model and bridge tests

Add automated tests for flow startup, cancellation, section selection, refresh, list projection,
article detail, save/unsave, related stories, native disposal, and bridge error propagation.

### MF-021: Responsive UI tests

Add tests for compact/expanded transitions, navigation, selection clearing, 50/50 and 60/40 pane
ratios, keyboard operation, and resize behavior.

### MF-022: Visual regression coverage

Capture and compare WPF and Compose screenshots for loading, loaded, empty, error, selected, saved,
detail, related-story, compact, and wide states.

### MF-023: Accessibility completion

Add accessible names for icon actions, visible focus states, logical keyboard traversal, semantic
headings, image descriptions, sufficient contrast, and screen-reader verification.

### MF-024: Windows CI coverage

CI should compile the MinGW presentation target, generate and pack the NuGet package, restore/build
WPF from that package, run bridge tests, and perform a launch smoke test on Windows.
