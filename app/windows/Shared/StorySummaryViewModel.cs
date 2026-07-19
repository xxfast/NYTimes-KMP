namespace NYTimes.Windows;

public sealed class StorySummaryViewModel(
    string uri,
    string title,
    string description,
    string sectionName,
    string byline,
    string imageUrl)
{
    public string Uri { get; } = uri;
    public string Title { get; } = title;
    public string Description { get; } = description;
    public string SectionName { get; } = sectionName;
    public string Byline { get; } = byline;
    public string ImageUrl { get; } = imageUrl;
    public bool HasImage => !string.IsNullOrWhiteSpace(ImageUrl);
}
