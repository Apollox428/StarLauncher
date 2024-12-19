using System.Collections.Generic;

using Windows.UI.Xaml.Controls;

namespace DreamLauncher.Views
{
  public sealed partial class Page1 : Page
  {
    public Page1()
    {
      InitializeComponent();

      var items = new List<string>();

      for (int i = 0; i < 10; i++)
      {
        items.Add($"Item {i}");
      }

      ContentGridView.ItemsSource = items;
      
      var featuredItems = new List<string>();
      for (int i = 0; i < 10; i++)
      {
        featuredItems.Add($"Item {i}");
      }

    }
  }
}
