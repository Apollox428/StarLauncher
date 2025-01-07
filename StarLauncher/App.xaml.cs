using System;
using System.Diagnostics;

using StarLauncher.Helpers;
using StarLauncher.Records;
using StarLauncher.Services;
using StarLauncher.ViewModels;
using StarLauncher.Views;

using Windows.ApplicationModel;
using Windows.ApplicationModel.Activation;
using Windows.ApplicationModel.AppService;
using Windows.ApplicationModel.Background;
using Windows.ApplicationModel.Core;
using Windows.Data.Xml.Dom;
using Windows.Foundation.Metadata;
using Windows.UI.Core;
using Windows.UI.Notifications;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Navigation;
using Version = StarLauncher.Records.Version;

namespace StarLauncher
{
    sealed partial class App : Application
    {
        public static BackgroundTaskDeferral AppServiceDeferral = null;
        public static AppServiceConnection Connection = null;
        public static event EventHandler AppServiceDisconnected;
        public static event EventHandler<AppServiceTriggerDetails> AppServiceConnected;
        public static bool IsForeground = false;

        public static MainViewModel ViewModel { get; } = new MainViewModel();

        public App()
        {
            InitializeComponent();
            Suspending += OnSuspending;
            EnteredBackground += App_EnteredBackground;
            LeavingBackground += App_LeavingBackground;

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

            StartFullTrustExtension();
        }

        private void App_LeavingBackground(object sender, LeavingBackgroundEventArgs e)
        {
            IsForeground = true;
        }

        private void App_EnteredBackground(object sender, EnteredBackgroundEventArgs e)
        {
            IsForeground = false;
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
        }

        public async void StartFullTrustExtension()
        {
            if (ApiInformation.IsApiContractPresent("Windows.ApplicationModel.FullTrustAppContract", 1, 0))
            {
                await FullTrustProcessLauncher.LaunchFullTrustProcessForCurrentAppAsync();
            }
        }

        protected override void OnBackgroundActivated(BackgroundActivatedEventArgs args)
        {
            base.OnBackgroundActivated(args);

            if (args.TaskInstance.TriggerDetails is AppServiceTriggerDetails details)
            {
                // only accept connections from callers in the same package
                if (details.CallerPackageFamilyName == Package.Current.Id.FamilyName)
                {
                    // connection established from the fulltrust process
                    AppServiceDeferral = args.TaskInstance.GetDeferral();
                    args.TaskInstance.Canceled += OnTaskCanceled;

                    Connection = details.AppServiceConnection;
                    //Connection.RequestReceived += OnRequestReceived;
                    AppServiceConnected?.Invoke(this, args.TaskInstance.TriggerDetails as AppServiceTriggerDetails);
                }
            }
        }

        private async void OnRequestReceived(AppServiceConnection sender, AppServiceRequestReceivedEventArgs args)
        {
            if (args.Request.Message.ContainsKey("data"))
            {
                object message = null;
                args.Request.Message.TryGetValue("data", out message);
                if (App.IsForeground)
                {
                    await CoreApplication.MainView.Dispatcher.RunAsync(CoreDispatcherPriority.Normal, new DispatchedHandler(async () =>
                    {
                        if (message != null)
                        {
                            MessageDialog dialog = new MessageDialog(message.ToString());
                            await dialog.ShowAsync();
                        }
                    }));
                }
                else
                {
                    ShowToast(message.ToString());
                }
            }

            if (args.Request.Message.ContainsKey("exit"))
            {
                App.Current.Exit();
            }
        }

        public static void ShowToast(string message)
        {
            ToastTemplateType toastTemplate = ToastTemplateType.ToastText02;
            XmlDocument toastXml = ToastNotificationManager.GetTemplateContent(toastTemplate);

            XmlNodeList toastTextElements = toastXml.GetElementsByTagName("text");
            toastTextElements[0].AppendChild(toastXml.CreateTextNode("Systray"));
            toastTextElements[1].AppendChild(toastXml.CreateTextNode(message));

            ToastNotification toast = new ToastNotification(toastXml);
            ToastNotificationManager.CreateToastNotifier().Show(toast);
        }

        private void OnTaskCanceled(IBackgroundTaskInstance sender, BackgroundTaskCancellationReason reason)
        {
            AppServiceDeferral?.Complete();
            AppServiceDeferral = null;
            Connection = null;
            AppServiceDisconnected?.Invoke(this, null);
        }

        private void OnSuspending(object sender, SuspendingEventArgs e)
        {
            var deferral = e.SuspendingOperation.GetDeferral();
            deferral.Complete();
        }

        void OnNavigationFailed(object sender, NavigationFailedEventArgs e)
        {
            throw new Exception("Failed to load Page " + e.SourcePageType.FullName);
        }
    }
}