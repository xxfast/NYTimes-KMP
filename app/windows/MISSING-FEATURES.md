# Missing Features

Capabilities still missing or incomplete for the Windows hosts (WPF + WinUI 3) and
`kotlin-native-nuget` integration. Broken behavior is tracked in [`BUGS.md`](BUGS.md).

Toolchain, module chain, and build commands:
[`BUGS.md` context](BUGS.md#context).

| Items | Owner |
|---|---|
| MF-002, MF-003 | `kotlin-native-nuget` |
| MF-004 | Decompose Router |
| MF-005+ | NYTimes-KMP sample / build / UI |

## Kotlin and NuGet

### MF-002: Direct `StateFlow<T>` export

Windows host VMs expose `Flow<T>` for NuGet. Prefer `StateFlow<T>` (current value + collect)
once the plugin supports it. Alpha02 still only lists `kotlinx.coroutines.flow.Flow`.

### MF-003: Transitive model export

Exposing a dependency-module type from a flow or method should auto-generate the C# wrapper and
native exports. Local NuGet DTOs in `:app:windows` are still required (see BUG-004).

### MF-004: MinGW support in Decompose Router

No MinGW artifact, so Windows host VMs cannot use `RouterContext`. Hosts own lifecycle
directly; Compose/iOS restore route state via adapters.

### MF-005: Automated NuGet production and restore

.NET builds do not run `:app:windows:packNuget` automatically. Needs a reliable task/script with
correct incremental inputs.

### MF-006: Local development package versioning

Package stays at `0.1.0`. A dev or generated version should avoid stale-cache ambiguity without
deleting `obj/packages/nytimes.kotlin` before restore. Host apps must also pick up a matching
`nytimes.dll` (package native assets on each host project).

### MF-007: Additional Windows architectures

Only `win-x64` is packaged and tested.

## Host runtime (WPF + WinUI)

### MF-008: Explicit loading, error, empty, and retry states

State models need failure info; both hosts need error/empty UI and retry (closes the gap left by
BUG-002).

### MF-009: Verified end-to-end data loading

Build/smoke pass, but section changes, selection, save/unsave, and related stories are not yet
verified together in both running apps.

### MF-010: Navigation and state restoration

Selected detail pane only — no history, compact back, or process-restore of section/article.

### MF-011: Runtime diagnostics

No structured logs or visible diagnostic state for flow, network, or bridge failures.

## Compose design parity

Applies to both WPF and WinUI unless noted.

### MF-012: Article images

List/detail images are partially wired; still need robust async load, sizing, crop, cache, and
error placeholders.

### MF-013: Responsive compact / expanded layouts

- Compact: full-screen list → detail with Back
- Expanded: 50/50 list/detail
- Wide detail: 60/40 article/related

### MF-014: Masthead, attribution, and icon parity

Compose vectors → host icon/resources (masthead, attribution, refresh, back, favourite,
fullscreen, external-link, error).

### MF-015: Theme and typography resources

Reusable theme dictionaries matching Compose Material presentation.

### MF-016: Top-story card parity

Images, adaptive min width, selected state, typography/spacing, favourite count, attribution.

### MF-017: Story-detail parity

Hero image (present), Back, fullscreen/side-panel, favourite icon, external URL, categories,
image metadata, Compose-like spacing/type.

### MF-018: Related-story navigation

Related items should open/replace detail when selected.

### MF-019: Favourite-section parity

Count label, loading, saved presentation, and full favourite-list validation.

## Quality and verification

### MF-020: View-model and bridge tests

Flow lifecycle, section/refresh, list projection, detail, save, related, disposal, errors
(Shared + hosts).

### MF-021: Responsive UI tests

Compact/expanded, navigation, selection, pane ratios, keyboard, resize — per host.

### MF-022: Visual regression

Screenshot compare vs Compose for loading/loaded/empty/error/selected/saved/detail/related/layout
states (WPF and WinUI).

### MF-023: Accessibility

Accessible names, focus, keyboard order, headings, image descriptions, contrast, screen readers.

### MF-024: Windows CI

Compile MinGW, pack NuGet, restore/build Windows.sln (Shared + WpfApp + WinUiApp), bridge tests,
launch smoke on Windows.
