using System.ComponentModel;

namespace NYTimes.Windows;

public sealed class SectionViewModel(string name) : INotifyPropertyChanged
{
    private bool _isSelected;
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

    public event PropertyChangedEventHandler? PropertyChanged;
}
