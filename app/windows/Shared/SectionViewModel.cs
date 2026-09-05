using System.ComponentModel;

namespace NYTimes.Windows;

public sealed class SectionViewModel(string name) : INotifyPropertyChanged
{
    private bool _isSelected;
    private int? _count;

    public string Name { get; } = name;

    public bool IsSelected
    {
        get => _isSelected;
        set
        {
            if (_isSelected == value) return;
            _isSelected = value;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(IsSelected)));
        }
    }

    /// <summary>Item count shown next to the name; null hides it (only favourites reports one).</summary>
    public int? Count
    {
        get => _count;
        set
        {
            if (_count == value) return;
            _count = value;
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(Count)));
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(nameof(DisplayName)));
        }
    }

    /// <summary>"favourites (3)" when a positive count is known, otherwise just the name.</summary>
    public string DisplayName => Count is > 0 ? $"{Name} ({Count})" : Name;

    public event PropertyChangedEventHandler? PropertyChanged;
}
