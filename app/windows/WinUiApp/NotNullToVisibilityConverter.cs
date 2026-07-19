using Microsoft.UI.Xaml;
using Microsoft.UI.Xaml.Data;

namespace WinUiApp;

/// <summary>Visible when value is non-null.</summary>
public sealed class NotNullToVisibilityConverter : IValueConverter
{
    public object Convert(object value, Type targetType, object parameter, string language) =>
        value is not null ? Visibility.Visible : Visibility.Collapsed;

    public object ConvertBack(object value, Type targetType, object parameter, string language) =>
        throw new NotSupportedException();
}
