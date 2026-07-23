namespace NYTimes.MauiApp;

public partial class MainPage : ContentPage
{
    public MainPage()
    {
        InitializeComponent();

        try
        {
            var sections = global::NYTimes.Kotlin.WindowsApp.SectionNames();
            var message = $"Kotlin bridge connected · {sections.Count} sections exported";
            BridgeStatusLabel.Text = message;
            Console.WriteLine(message);
        }
        catch (Exception exception)
        {
            var message = $"Kotlin bridge failed: {exception.Message}";
            BridgeStatusLabel.Text = message;
            Console.Error.WriteLine(message);
        }
    }
}
