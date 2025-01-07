using System;
using System.Diagnostics;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Toolkit.Uwp.Helpers;
using Microsoft.Toolkit.Uwp.UI.Media;
using StarLauncher.ViewModels;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;

namespace StarLauncher.Helpers
{
    public static class AnimationHelper
    {
        public static void Fade(TextBlock textBlock, string text, int durationInMs = 250)
        {
            // Create a fade-out animation
            var fadeOut = new DoubleAnimation
            {
                To = 0,
                Duration = TimeSpan.FromMilliseconds(durationInMs)
            };

            var fadeIn = new DoubleAnimation
            {
                To = 1,
                Duration = TimeSpan.FromMilliseconds(durationInMs)
            };

            // Handle text change on fade-out completion
            fadeOut.Completed += (s, e) =>
            {
                textBlock.Text = text; // Update text dynamically
                                       // Begin fade-in animation
                var fadeInStoryboard = new Storyboard();
                Storyboard.SetTarget(fadeIn, textBlock);
                Storyboard.SetTargetProperty(fadeIn, "Opacity");
                fadeInStoryboard.Children.Add(fadeIn);
                fadeInStoryboard.Begin();
            };

            // Begin fade-out animation
            var fadeOutStoryboard = new Storyboard();
            Storyboard.SetTarget(fadeOut, textBlock);
            Storyboard.SetTargetProperty(fadeOut, "Opacity");
            fadeOutStoryboard.Children.Add(fadeOut);
            fadeOutStoryboard.Begin();
        }

        public static void Crossfade(UIElement oldElement, UIElement newElement, double durationInSeconds = 0.5)
        {
            // Fade out animation for the old element
            var fadeOut = new DoubleAnimation
            {
                To = 0,
                Duration = TimeSpan.FromSeconds(durationInSeconds),
            };

            // Fade in animation for the new element
            var fadeIn = new DoubleAnimation
            {
                To = 1,
                Duration = TimeSpan.FromSeconds(durationInSeconds),
            };

            // Apply animations to the elements
            Storyboard.SetTarget(fadeOut, oldElement);
            Storyboard.SetTargetProperty(fadeOut, "Opacity");

            Storyboard.SetTarget(fadeIn, newElement);
            Storyboard.SetTargetProperty(fadeIn, "Opacity");

            // Create and start the storyboard
            var storyboard = new Storyboard();
            storyboard.Children.Add(fadeOut);
            storyboard.Children.Add(fadeIn);

            storyboard.Begin();
        }

        public static void TransitionShadowColor(AttachedCardShadow shadowElement, Color toColor, double durationInSeconds = 0.25)
        {
            // Create a ColorAnimation
            var colorAnimation = new ColorAnimation
            {
                To = toColor,
                From = shadowElement.Color,
                Duration = TimeSpan.FromSeconds(durationInSeconds)
            };

            // Create a Storyboard to host the animation
            var storyboard = new Storyboard();

            // Set the animation target
            Storyboard.SetTarget(colorAnimation, shadowElement);
            Storyboard.SetTargetProperty(colorAnimation, "(media:AttachedCardShadow.Color)");

            // Add the animation to the Storyboard and start it
            storyboard.Children.Add(colorAnimation);
            storyboard.Begin();
        }

        public static async Task InterpolateAccentColor(MainViewModel viewModel, Color fromColor, Color toColor, TimeSpan duration, CancellationToken token)
        {
            token.ThrowIfCancellationRequested();
            int steps = 60; // Number of animation steps
            double interval = duration.TotalMilliseconds / steps; // Time per step in milliseconds

            for (int i = 0; i <= steps; i++)
            {
                double t = (double)i / steps; // Progress factor (0 to 1)

                // Interpolate each color component
                var interpolatedColor = Color.FromArgb(
                    (byte)(fromColor.A + (toColor.A - fromColor.A) * t), // Alpha
                    (byte)(fromColor.R + (toColor.R - fromColor.R) * t), // Red
                    (byte)(fromColor.G + (toColor.G - fromColor.G) * t), // Green
                    (byte)(fromColor.B + (toColor.B - fromColor.B) * t)  // Blue
                );

                // Apply the interpolated color to the shadow
                viewModel.CurrentAccentColor = interpolatedColor.ToHex();
                Debug.WriteLine($"Interpolated Hex: {interpolatedColor.ToHex()}");

                // Wait for the next frame
                await Task.Delay((int)interval);
            }
        }


    }

}