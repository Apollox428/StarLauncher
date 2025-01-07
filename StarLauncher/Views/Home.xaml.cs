using System;
using System.Collections.Generic;
using System.Diagnostics;
using Microsoft.Toolkit.Uwp.Helpers;
using StarLauncher.Helpers;
using StarLauncher.ViewModels;
using Windows.UI;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;

namespace StarLauncher.Views
{
    public sealed partial class Home : Page
    {
        public MainViewModel ViewModel => App.ViewModel;
        public Home()
        {
            InitializeComponent();
            var items = new List<string>();
        
            for (int i = 0; i < 10; i++)
            {
                items.Add($"Item {i}");
            }

            ContentGridView.ItemsSource = items;
            FeaturedContent.SelectionChanged += FeaturedContent_SelectionChanged;
            if (FeaturedContent.SelectedIndex >= 0 && FeaturedContent.SelectedIndex < ViewModel.FeaturedItems.Count) { Glow.Color = ViewModel.FeaturedItems[FeaturedContent.SelectedIndex].AccentColor.ToColor(); }
        }

        private void FeaturedContent_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (FeaturedContent.SelectedIndex >= 0 && FeaturedContent.SelectedIndex < ViewModel.FeaturedItems.Count)
            {
                var selectedItem = ViewModel.FeaturedItems[FeaturedContent.SelectedIndex];
                string accentColor = selectedItem.AccentColor;
                ViewModel.CurrentAccentColor = accentColor;
                DynamicTextFade(FeaturedName, selectedItem.Name);
                DynamicTextFade(FeaturedDescription, selectedItem.Description);
                DynamicTextFade(FeaturedButton, selectedItem.ButtonText);
            }
        }

        private void DynamicTextFade(TextBlock textBlock, string text) { 
            if (textBlock.Text != text) { AnimationHelper.Fade(textBlock, text); }
        }

    }
}
