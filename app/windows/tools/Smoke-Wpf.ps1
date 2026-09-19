<#
.SYNOPSIS
  Launches the WPF host, optionally seeded with a story, screenshots it at the compact,
  expanded and wide breakpoints, and prints the diagnostics log.

.DESCRIPTION
  A repeatable smoke run for the desktop host: it proves the bridge bootstraps, the state
  flow collects, a story restores, and the layout survives every breakpoint. The PNGs it
  writes are the baseline for visual comparison against the Compose screens.

  Run from the repository root after `dotnet build app\windows\WpfApp\WpfApp.csproj -p:Platform=x64`.

.PARAMETER OutDir
  Where to write the screenshots. Defaults to app\windows\build\screenshots.

.PARAMETER Section
  Section to restore on launch, e.g. world. Omit to start on home.

.PARAMETER StoryUri
  nyt://article/... URI to open on launch. Needs -Section and -StoryTitle too.

.PARAMETER Seconds
  How long to give the app to load before the first screenshot. Defaults to 14.
#>
param(
    [string]$OutDir = "app\windows\build\screenshots",
    [string]$Section,
    [string]$StoryUri,
    [string]$StoryTitle = "Seeded story",
    [int]$Seconds = 14
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing
Add-Type @"
using System; using System.Runtime.InteropServices;
public struct RECT { public int Left, Top, Right, Bottom; }
public static class SmokeWin32 {
  [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
  [DllImport("user32.dll")] public static extern bool MoveWindow(IntPtr h, int x, int y, int w, int ht, bool r);
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr h);
}
"@

$exe = Get-ChildItem "app\windows\WpfApp\bin\x64\Debug\net10.0-windows\win-x64\WpfApp.exe" -ErrorAction Stop
$storage = Join-Path $env:LOCALAPPDATA "NYTimes-KMP"
New-Item -ItemType Directory -Force $storage | Out-Null
New-Item -ItemType Directory -Force $OutDir | Out-Null

$statePath = Join-Path $storage "host-state.json"
$logPath = Join-Path $storage "diagnostics.log"
if ($Section -or $StoryUri) {
    $state = @{ Section = $Section; StoryUri = $StoryUri; StorySection = $Section; StoryTitle = $StoryTitle }
    $state | ConvertTo-Json -Compress | Set-Content -Encoding utf8 $statePath
}
Remove-Item -Force -ErrorAction SilentlyContinue $logPath

$process = Start-Process $exe.FullName -PassThru
try {
    Start-Sleep -Seconds $Seconds
    $hwnd = (Get-Process WpfApp | Select-Object -First 1).MainWindowHandle
    [SmokeWin32]::SetForegroundWindow($hwnd) | Out-Null

    foreach ($layout in @(
        @{ Name = "compact";  Width = 700;  Height = 720 },
        @{ Name = "expanded"; Width = 1180; Height = 720 },
        @{ Name = "wide";     Width = 1600; Height = 900 })) {
        [SmokeWin32]::MoveWindow($hwnd, 40, 40, $layout.Width, $layout.Height, $true) | Out-Null
        Start-Sleep -Seconds 3
        $rect = New-Object RECT
        [SmokeWin32]::GetWindowRect($hwnd, [ref]$rect) | Out-Null
        $bitmap = New-Object System.Drawing.Bitmap ($rect.Right - $rect.Left), ($rect.Bottom - $rect.Top)
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.CopyFromScreen($rect.Left, $rect.Top, 0, 0, $bitmap.Size)
        $file = Join-Path $OutDir "wpf-$($layout.Name).png"
        $bitmap.Save($file, [System.Drawing.Imaging.ImageFormat]::Png)
        Write-Output "Saved $file"
    }

    if ($process.HasExited) { throw "WpfApp exited early with code $($process.ExitCode)" }
    Write-Output "WpfApp still running after all breakpoints"
}
finally {
    Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
    Remove-Item -Force -ErrorAction SilentlyContinue $statePath
}

Write-Output "--- diagnostics.log"
Get-Content $logPath | Where-Object { $_ -notmatch "^\s+(ThreadId|DateTime)=" }
