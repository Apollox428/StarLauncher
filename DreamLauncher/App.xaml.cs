using System;
using System.Diagnostics;

using DreamLauncher.Helpers;
using DreamLauncher.Records;
using DreamLauncher.Services;
using DreamLauncher.Views;

using Windows.ApplicationModel.Activation;
using Windows.ApplicationModel.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;
using Version = DreamLauncher.Records.Version;

namespace DreamLauncher
{
  sealed partial class App : Application
  {
    public App()
    {
      InitializeComponent();

      SettingsHelper.CreateSettings();
            
      var version = new Version(
          type: VersionType.RELEASE,
          identifier: "fabric-1.19.2",
          releaseDate: DateTime.Now,
          loaderType: LoaderType.FABRIC
          );
      // Create an Installation instance
      var installation = new Installation(version);

      // Print Installation details
      Debug.WriteLine("Installation Details:");
      Debug.WriteLine($"Version: {installation.Version.Identifier}");
      Debug.WriteLine($"Display Name: {installation.DisplayName}");
      Debug.WriteLine($"ID: {installation.Id}");
    }

    public async void LaunchFilesystemAccessPage()
    {
        bool result = await Windows.System.Launcher.LaunchUriAsync(new Uri(@"http://www.bing.com"));
        Debug.WriteLine($"Succeeded: {result}");
    }

    public ThemeService ThemeService { get; private set; }

    protected override void OnLaunched(LaunchActivatedEventArgs e)
    {
      Frame rootFrame = Window.Current.Content as Frame;

      var coreTitleBar = CoreApplication.GetCurrentView().TitleBar;
      coreTitleBar.ExtendViewIntoTitleBar = true;

      ThemeService = new ThemeService(Window.Current);
      ThemeService.SetTheme();

      if (rootFrame == null)
      {
        rootFrame = new Frame();
        rootFrame.NavigationFailed += OnNavigationFailed;

        Window.Current.Content = rootFrame;
      }

      if (e.PrelaunchActivated == false)
      {
        if (rootFrame.Content == null)
        {
          rootFrame.Navigate(typeof(Main), e.Arguments);
        }

        Window.Current.Activate();
      }

      LaunchAppSettings();
    }

    public async void LaunchAppSettings()
    {
        bool result = await Windows.System.Launcher.LaunchUriAsync(new Uri("ms-settings:appsfeatures-app"));
    }

    void OnNavigationFailed(object sender, NavigationFailedEventArgs e)
    {
      throw new Exception("Failed to load Page " + e.SourcePageType.FullName);
    }
  }
}