# Missing Features

Capabilities still missing or incomplete for the desktop .NET hosts (WPF + WinUI 3 + .NET MAUI
on Windows/macOS) and `kotlin-native-nuget` integration. Broken behavior is tracked in
[`BUGS.md`](BUGS.md).

Toolchain, module chain, and build commands:
[`BUGS.md` context](BUGS.md#context).

| Items            | Owner                            |
|------------------|----------------------------------|
| BUG-009, BUG-010 | `kotlin-native-nuget`            |
| MF-004           | Decompose Router                 |
| MF-006+          | NYTimes-KMP sample / build / UI  |

## Kotlin and NuGet

### MF-002: Direct `StateFlow<T>` export — done (0.2.0)

Host VMs expose `StateFlow<T>` → `KotlinStateFlow<T>` (`IAsyncEnumerable<T>` + synchronous
`.Value`). Collect path unchanged; `.Value` available when hosts need a snapshot without await.

### MF-003: Transitive model export — done (0.3.0)

Plugin reachability (ADR-066) exports the shared state/model types directly:
`rootPackage = "io.github.xxfast.nytimes"` admits `screens.*` and `models` from `:app`. The local
NuGet DTOs and `InteropMappers` are deleted; `:app:windows` keeps only the host view models.
Value classes bind as `readonly record struct`, `Instant` as `DateTimeOffset`, nullable object
lists per ADR-075. Remaining plugin gaps: reserved-keyword escaping (BUG-009) and the
`error` parameter-name collision (BUG-010).

### MF-004: MinGW support in Decompose Router

No MinGW artifact, so Windows host VMs cannot use `RouterContext`. Hosts own lifecycle
directly; Compose/iOS restore route state via adapters.

### MF-006: Local development package versioning

Package is `0.2.0` (bumped with the plugin). A generated/dev version would still help avoid
stale-cache ambiguity without deleting `obj/packages/nytimes.kotlin` before restore. The automated
restore hook still evicts that repo-local cache so native assets match each host project.

### MF-007: Additional native architectures

`win-x64` and `osx-arm64` are packaged; the MAUI bridge is runtime-tested on
`maccatalyst-arm64`. Additional Windows architectures are not yet packaged.

## Host runtime (WPF + WinUI + MAUI)

### MF-008: Explicit loading, error, empty, and retry states

Loading is modelled as null articles/related (shared `Loading`) and failures as a message in
`TopStoriesState.failure` / `StoryState.failure` (BUG-002). The .NET hosts show the message with a
Retry button. Still missing: an empty state (section loaded with no stories), structured error
reasons (offline vs. HTTP status) instead of a raw exception message, and error UI in the
Compose / Wear screens, which still spin on failure.

### MF-009: Verified end-to-end data loading

Build/smoke pass, but section changes, selection, save/unsave, and related stories are not yet
verified together in every running app.

### MF-010: Navigation and state restoration

Selected detail pane only — no history, compact back, or process-restore of section/article.

### MF-011: Runtime diagnostics

No structured logs or visible diagnostic state for flow, network, or bridge failures.

## Compose design parity

Applies to WPF and WinUI unless noted. MAUI currently has a bridge-verified scaffold only.

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
(`numberOfFavourites: Int?` is on the exported state; hosts still need UI.)

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
