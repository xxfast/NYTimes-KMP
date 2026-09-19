using System.Windows;
using System.Windows.Controls;

namespace WpfApp;

/// <summary>
/// Lays children out in as many equal-width columns as fit at <see cref="MinItemWidth"/>, the
/// way Compose's <c>GridCells.Adaptive</c> does, so story cards reflow with the pane width.
/// </summary>
public sealed class AdaptiveGridPanel : Panel
{
    public static readonly DependencyProperty MinItemWidthProperty = DependencyProperty.Register(
        nameof(MinItemWidth),
        typeof(double),
        typeof(AdaptiveGridPanel),
        new FrameworkPropertyMetadata(248.0, FrameworkPropertyMetadataOptions.AffectsMeasure));

    public double MinItemWidth
    {
        get => (double)GetValue(MinItemWidthProperty);
        set => SetValue(MinItemWidthProperty, value);
    }

    protected override Size MeasureOverride(Size availableSize)
    {
        var width = double.IsInfinity(availableSize.Width) ? MinItemWidth : availableSize.Width;
        var columns = Columns(width);
        var itemWidth = width / columns;

        var rowHeight = 0.0;
        var height = 0.0;
        for (var index = 0; index < InternalChildren.Count; index++)
        {
            var child = InternalChildren[index];
            child.Measure(new Size(itemWidth, double.PositiveInfinity));
            rowHeight = Math.Max(rowHeight, child.DesiredSize.Height);
            if (index % columns == columns - 1 || index == InternalChildren.Count - 1)
            {
                height += rowHeight;
                rowHeight = 0;
            }
        }

        return new Size(width, height);
    }

    protected override Size ArrangeOverride(Size finalSize)
    {
        var columns = Columns(finalSize.Width);
        var itemWidth = finalSize.Width / columns;

        var top = 0.0;
        var rowHeight = 0.0;
        for (var index = 0; index < InternalChildren.Count; index++)
        {
            var child = InternalChildren[index];
            var column = index % columns;
            child.Arrange(new Rect(column * itemWidth, top, itemWidth, child.DesiredSize.Height));
            rowHeight = Math.Max(rowHeight, child.DesiredSize.Height);
            if (column == columns - 1)
            {
                top += rowHeight;
                rowHeight = 0;
            }
        }

        return finalSize;
    }

    private int Columns(double width) => Math.Max(1, (int)Math.Floor(width / Math.Max(1, MinItemWidth)));
}
