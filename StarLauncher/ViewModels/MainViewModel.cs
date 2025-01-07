//  ---------------------------------------------------------------------------------
//  Copyright (c) Microsoft Corporation.  All rights reserved.
// 
//  The MIT License (MIT)
// 
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
// 
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
// 
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//  ---------------------------------------------------------------------------------

using System.Collections.ObjectModel;
using Windows.System;
using System.Collections.Generic;
using Microsoft.Toolkit.Uwp;
using StarLauncher.Records;
using System;
using Windows.UI.Xaml.Controls;

namespace StarLauncher.ViewModels
{
    /// <summary>
    /// Provides data and commands accessible to the entire app.  
    /// </summary>
    public class MainViewModel : BindableBase
    {
        private DispatcherQueue dispatcherQueue = DispatcherQueue.GetForCurrentThread();

        /// <summary>
        /// Creates a new MainViewModel.
        /// </summary>
        public MainViewModel() => Init();

        /// <summary>
        /// Gets the list to display.
        /// </summary>
        public ObservableCollection<FeaturedItem> FeaturedItems { get; private set; } = new ObservableCollection<FeaturedItem>();

        /// <summary>
        /// Gets the unfiltered list. 
        /// </summary>
        private List<FeaturedItem> _FeaturedItems { get; } = new List<FeaturedItem>();

        private string _currentAccentColor = "#00000000";
        public string CurrentAccentColor
        {
            get => _currentAccentColor;
            set => Set(ref _currentAccentColor, value);
        }

        private void Init()
        {
            AddFeaturedItem("Continue playing", "Pick up where you left off", "", "Launch", "https://ccdn.g-portal.com/large_Gallery_Blog_MC_Screenshots_0001_2_b33993315b.jpg");
            AddFeaturedItem("Most played", "People seem to like this one", "", "Launch", "https://minecraft.wiki/images/thumb/The_Bountiful_Update.png/800px-The_Bountiful_Update.png?a2202");
            AddFeaturedItem("Latest release", "Fresh from the oven", "", "Launch", "https://www.minecraft.net/content/dam/minecraftnet/games/minecraft/key-art/MCV_holidaydrop_editorial_launch_1170x500.jpg");
            AddFeaturedItem("Awesome server", "Generic description", "", "Connect", "ms-appx:///Images/HeroWallpaper.jpg");
            AddFeaturedItem("Extraordinary map", "Generic description 2", "", "Try it out", "ms-appx:///Images/HeroWallpaper.jpg");
        }

        private async void AddFeaturedItem(
            string name,
            string description,
            string action,
            string buttonText,
            string imageUri
        )
        {
            var featuredItem = new FeaturedItem(name, description, action, buttonText, imageUri);
            _FeaturedItems.Add(featuredItem);
            Update();
            await featuredItem.InitializeAccentColorAsync();
        }

        public void Update()
        {
            FeaturedItems.Clear();
            foreach (FeaturedItem item in _FeaturedItems)
            {
                FeaturedItems.Add(item);
            }
        }
    }
}