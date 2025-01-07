using ColorThiefDotNet;
using System.Diagnostics;
using System.Threading.Tasks;
using System;
using Windows.Graphics.Imaging;
using Windows.Storage.Streams;

namespace StarLauncher.Records
{
    public class FeaturedItem
    {
        public string Name { get; }
        public string Description { get; }
        public string Action { get; set; }
        public string ButtonText { get; set; }
        public string ImageUri { get; }
        public string AccentColor { get; private set; }

        public FeaturedItem(
            string name,
            string description,
            string action,
            string buttonText,
            string imageUri
        )
        {
            Name = name;
            Description = description;
            Action = action;
            ButtonText = buttonText;
            ImageUri = imageUri;
            AccentColor = "#00000000";
        }

        public async Task InitializeAccentColorAsync()
        {
            AccentColor = await GetAccentColorAsync(ImageUri);
        }

        private async Task<string> GetAccentColorAsync(string imageUri)
        {
            try
            {
                RandomAccessStreamReference random = RandomAccessStreamReference.CreateFromUri(new Uri(imageUri));
                using (IRandomAccessStream stream = await random.OpenReadAsync())
                {
                    if (stream == null) throw new Exception("Stream is null");

                    stream.Seek(0);
                    var decoder = await BitmapDecoder.CreateAsync(stream);
                    var colorThief = new ColorThief();
                    var color = await colorThief.GetColor(decoder);

                    return color.Color.ToHexAlphaString();
                }
            }
            catch (Exception ex)
            {
                Debug.WriteLine($"Error extracting accent color: {ex.Message}. Retrying...");
                try
                {
                    RandomAccessStreamReference random = RandomAccessStreamReference.CreateFromUri(new Uri(imageUri));
                    using (IRandomAccessStream stream = await random.OpenReadAsync())
                    {
                        if (stream == null) throw new Exception("Stream is null");

                        stream.Seek(0);
                        var decoder = await BitmapDecoder.CreateAsync(stream);
                        var colorThief = new ColorThief();
                        var color = await colorThief.GetColor(decoder);

                        return color.Color.ToHexAlphaString();
                    }
                }
                catch (Exception finalException)
                {
                    Debug.WriteLine($"Error extracting accent color: {finalException.Message}");
                    return "#FFFFFF"; // Default to white
                }
            }
        }
    }
}