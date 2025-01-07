using System;
using System.Collections.ObjectModel;
using Windows.ApplicationModel.AppService;
using Windows.UI.Composition;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Hosting;
using Windows.UI.Xaml.Media;

// The Blank Page item template is documented at https://go.microsoft.com/fwlink/?LinkId=234238

namespace StarLauncher.Views
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Instances : Page
    {
        private Compositor _compositor;
        private LoadedImageSurface _backgroundSurface;
        private LoadedImageSurface _maskSurface;

        private ObservableCollection<Line> _lines = new ObservableCollection<Line>();


        public Instances()
        {
            InitializeComponent();
            HeroMaskedImageGrid.Loaded += (sender, _) => LoadBackground();
            HeroMaskedImageGrid.Unloaded += (sender, _) => UnloadBackground();
            App.AppServiceConnected += (sender, args) =>
            {
                App.Connection.RequestReceived += OnRequestReceived;
            };
        }

        private void LoadBackground()
        {
            if (_compositor is null)
            {
                _compositor = Window.Current.Compositor;
            }

            //// Load images only once
            if (_backgroundSurface is null)
            {
                _backgroundSurface = LoadedImageSurface.StartLoadFromUri(new Uri("ms-appx:///Images/HeroWallpaper.jpg"));
            }

            if (_maskSurface is null)
            {
                _maskSurface = LoadedImageSurface.StartLoadFromUri(new Uri("ms-appx:///Images/MaskAlt.png"));
            }

            var imageBrush = _compositor.CreateSurfaceBrush(_backgroundSurface);
            imageBrush.Stretch = CompositionStretch.UniformToFill; //// Preserves image ratio and fills entire container (stack panel, grid, etc)

            var maskBrush = _compositor.CreateSurfaceBrush(_maskSurface);
            maskBrush.Stretch = CompositionStretch.Fill; //// Stretches mask image to the size of container

            var maskedBrush = _compositor.CreateMaskBrush();
            maskedBrush.Source = imageBrush;
            maskedBrush.Mask = maskBrush;

            var size = HeroMaskedImageGrid.ActualSize;

            var outputSpriteVisual = _compositor.CreateSpriteVisual();
            outputSpriteVisual.Size = size;
            outputSpriteVisual.Brush = maskedBrush;

            ElementCompositionPreview.SetElementChildVisual(HeroMaskedImageGrid, outputSpriteVisual);
        }

        private void UnloadBackground()
        {
            _backgroundSurface?.Dispose();
            _maskSurface?.Dispose();
        }

        private void RefreshBackground(object sender, SizeChangedEventArgs e)
        {
            LoadBackground();
        }

        private async void OnRequestReceived(AppServiceConnection sender, AppServiceRequestReceivedEventArgs args)
        {
        if (args.Request.Message.ContainsKey("data"))
        {
        object message;
        args.Request.Message.TryGetValue("data", out message);
        if (message != null)
        {
        var parser = new AnsiCodeParser();
        var processedText = parser.ProcessAnsiCodes(message.ToString());

        await Dispatcher.RunAsync(CoreDispatcherPriority.Normal, () =>
        {
        foreach (var formattedText in processedText)
        {
        // Add each line dynamically
        AddLine(formattedText.Text, formattedText.IsBold, formattedText.IsUnderline, formattedText.ForegroundColor);
        }
        });
        }
        }
        }

        private void AddLine(string text, bool isBold, bool isUnderline, Windows.UI.Color color)
        {
        // Create a new TextBlock for the line
        var textBlock = new TextBlock
        {
            Text = text,
            Foreground = new SolidColorBrush(color),
            FontFamily = new Windows.UI.Xaml.Media.FontFamily("Consolas"), // Monospace for alignment
            FontSize = 14,
            Margin = new Thickness(0, 2, 0, 2),
            TextWrapping = TextWrapping.Wrap, // Allows long text to wrap
            TextTrimming = TextTrimming.Clip, // Ensures text does not get ellipses
            HorizontalAlignment = HorizontalAlignment.Left
        };

        // Apply bold and underline if specified
        if (isBold)
        {
        textBlock.FontWeight = Windows.UI.Text.FontWeights.Bold;
        }

        if (isUnderline)
        {
        textBlock.TextDecorations = Windows.UI.Text.TextDecorations.Underline;
        }

        // Add the TextBlock to the StackPanel
        OutputPanel.Children.Add(textBlock);

        // Automatically scroll to the bottom
        OutputScrollViewer.ChangeView(null, OutputScrollViewer.ScrollableHeight, null);
        }



    }
}
