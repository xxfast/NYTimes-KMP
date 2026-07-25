using System.Globalization;

namespace NYTimes.MauiApp;

public sealed class ValuePresenceConverter : IValueConverter
{
    public object Convert(object? value, Type targetType, object? parameter, CultureInfo culture)
    {
        var isPresent = value is not null;
        return string.Equals(parameter?.ToString(), "Invert", StringComparison.OrdinalIgnoreCase)
            ? !isPresent
            : isPresent;
    }

    public object ConvertBack(object? value, Type targetType, object? parameter, CultureInfo culture) =>
        throw new NotSupportedException();
}
