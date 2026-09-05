using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace WpfApp;

/// <summary>
/// Loads a remote image decoded to the control's laid-out width, the way the Compose
/// <c>ArticleImage</c> decodes to its constraints, and shows a spinner while downloading or an
/// icon when the download or decode fails. Downloads go through WPF's bitmap cache.
/// </summary>
public partial class ArticleImage : UserControl
{
    public static readonly DependencyProperty SourceProperty = DependencyProperty.Register(
        nameof(Source),
        typeof(string),
        typeof(ArticleImage),
        new PropertyMetadata(null, (d, _) => ((ArticleImage)d).Reload()));

    private string? _loadedUrl;

    public ArticleImage()
    {
        InitializeComponent();
        // The first layout pass gives us a width to decode to; until then the source waits.
        SizeChanged += (_, _) => { if (_loadedUrl is null) Reload(); };
    }

    /// <summary>Absolute image URL; null or empty clears the picture.</summary>
    public string? Source
    {
        get => (string?)GetValue(SourceProperty);
        set => SetValue(SourceProperty, value);
    }

    private void Reload()
    {
        var url = Source;
        _loadedUrl = null;
        Picture.Source = null;

        if (string.IsNullOrWhiteSpace(url))
        {
            ShowState(loading: false, failed: false);
            return;
        }

        if (!Uri.TryCreate(url, UriKind.Absolute, out var uri))
        {
            ShowState(loading: false, failed: true);
            return;
        }

        if (ActualWidth < 1)
        {
            // Not laid out yet; SizeChanged re-enters once there is a width to decode to.
            ShowState(loading: true, failed: false);
            return;
        }

        var scale = VisualTreeHelper.GetDpi(this).DpiScaleX;
        var bitmap = new BitmapImage();
        bitmap.BeginInit();
        bitmap.UriSource = uri;
        bitmap.CacheOption = BitmapCacheOption.OnLoad;
        bitmap.DecodePixelWidth = (int)Math.Ceiling(ActualWidth * scale);
        bitmap.EndInit();

        _loadedUrl = url;
        Picture.Source = bitmap;

        if (bitmap.IsDownloading)
        {
            ShowState(loading: true, failed: false);
            bitmap.DownloadCompleted += (_, _) => { if (_loadedUrl == url) ShowState(loading: false, failed: false); };
            bitmap.DownloadFailed += (_, _) => { if (_loadedUrl == url) ShowState(loading: false, failed: true); };
            bitmap.DecodeFailed += (_, _) => { if (_loadedUrl == url) ShowState(loading: false, failed: true); };
        }
        else
        {
            ShowState(loading: false, failed: false);
        }
    }

    private void ShowState(bool loading, bool failed)
    {
        Loading.Visibility = loading ? Visibility.Visible : Visibility.Collapsed;
        Failed.Visibility = failed ? Visibility.Visible : Visibility.Collapsed;
        Picture.Visibility = !loading && !failed && Picture.Source is not null
            ? Visibility.Visible
            : Visibility.Collapsed;
    }
}
