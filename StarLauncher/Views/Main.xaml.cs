using System;
using System.Linq;

using Microsoft.UI.Xaml.Controls;
using Windows.ApplicationModel.AppService;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;

namespace StarLauncher.Views
{
  public sealed partial class Main : Page
  {
    private int lastIndex = 0;
    public Main()
    {
      InitializeComponent();

      if (Environment.OSVersion.Version.Build >= 22000)
      {
        SetValue(BackdropMaterial.ApplyToRootOrPageBackgroundProperty, true);
      }

      Window.Current.SetTitleBar(TitleBarGrid);

      Window.Current.Activated += (s, e) =>
      {
        TitleBarGrid.Opacity = e.WindowActivationState != CoreWindowActivationState.Deactivated ? 1 : 0.5;
      };

      NavigationViewControl.SelectedItem = NavigationViewControl.MenuItems.First();

      ContentFrame.Navigate(typeof(Home));
    }

    public async void sendTestMessage()
    {
        if (App.Connection != null) {
            ValueSet request = new ValueSet();
            request.Add("data", "Hi! ✨");
            AppServiceResponse response = await App.Connection.SendMessageAsync(request);
        }
    }

    private void Navigate(Microsoft.UI.Xaml.Controls.NavigationView sender, Microsoft.UI.Xaml.Controls.NavigationViewItemInvokedEventArgs args)
    {
      var index = sender.MenuItems.IndexOf(args.InvokedItemContainer);

      if (index == -1)
      {
        ContentFrame.Navigate(typeof(Settings));

        return;
      }

      if (index != -1 && index == lastIndex)
      {
        return;
      }

      lastIndex = index;

      switch (index)
      {
            case 0:
                ContentFrame.Navigate(typeof(Home)); 
                break;
            case 1:
                ContentFrame.Navigate(typeof(Instances));
                break;
            case 2:
                ContentFrame.Navigate(typeof(Servers));
                break;
            case 3:
                ContentFrame.Navigate(typeof(Friends));
                break;
            case 4:
                ContentFrame.Navigate(typeof(Explore));
                break;
            default: break;
      }
    }
  }
}