//*********************************************************  
//  
// Copyright (c) Microsoft. All rights reserved.  
// This code is licensed under the MIT License (MIT).  
// THIS CODE IS PROVIDED *AS IS* WITHOUT WARRANTY OF  
// ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING ANY  
// IMPLIED WARRANTIES OF FITNESS FOR A PARTICULAR  
// PURPOSE, MERCHANTABILITY, OR NON-INFRINGEMENT.  
//  
//********************************************************* 
using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;
using System.Threading.Tasks;
using Windows.ApplicationModel;
using Windows.ApplicationModel.Core;
using Windows.ApplicationModel.AppService;
using Windows.Foundation.Collections;
using Windows.UI.Popups;
using System.Diagnostics;

namespace DesktopProxy
{
    class ProxyApplicationContext : ApplicationContext
    {
        private AppServiceConnection connection = null;
        private NotifyIcon notifyIcon = null;
        private Launcher launcherInstance = new Launcher();

        public ProxyApplicationContext()
        {
            MenuItem openMenuItem = new MenuItem("Open", new EventHandler(OpenApp));
            MenuItem sendToUWP = new MenuItem("Send message", new EventHandler(SendToUWP));
            MenuItem exitMenuItem = new MenuItem("Exit", new EventHandler(Exit));
            openMenuItem.DefaultItem = true;

            notifyIcon = new NotifyIcon();
            notifyIcon.Click += new EventHandler(OpenApp);
            notifyIcon.Icon = Properties.Resources.Icon;
            notifyIcon.ContextMenu = new ContextMenu(new MenuItem[]{ openMenuItem, sendToUWP, exitMenuItem });
            notifyIcon.Visible = true;
        }

        private async void OpenApp(object sender, EventArgs e)
        {
            IEnumerable<AppListEntry> appListEntries = await Package.Current.GetAppListEntriesAsync();
            await appListEntries.First().LaunchAsync();
            await CheckConnection();
        }

        private void SendToUWP(object sender, EventArgs e)
        {
            launcherInstance.Test("cmd.exe", new string[] { "/c ping google.com" }, p_OutputDataReceived);
        }

        private async void p_OutputDataReceived(object sender, DataReceivedEventArgs data)
        {
            Debug.WriteLine(data.Data);
            ValueSet message = new ValueSet();
            message.Add("data", data.Data);
            await Send(message);
        }

        private async void Exit(object sender, EventArgs e)
        {
            ValueSet message = new ValueSet();
            message.Add("exit", "");
            await Send(message);
            Application.Exit();
        }

        private async Task CheckConnection() {
            if (connection == null)
            {
                connection = new AppServiceConnection();
                connection.PackageFamilyName = Package.Current.Id.FamilyName;
                connection.AppServiceName = "StarLauncherService";
                connection.ServiceClosed += Connection_ServiceClosed;
                connection.RequestReceived += OnRequestReceived;
                AppServiceConnectionStatus connectionStatus = await connection.OpenAsync();
                if (connectionStatus != AppServiceConnectionStatus.Success)
                {
                    MessageBox.Show("Status: " + connectionStatus.ToString());
                    return;
                }
            }
        }

        private async Task Send(ValueSet message)
        {
            await CheckConnection();
            await connection.SendMessageAsync(message);
        }

        private void Connection_ServiceClosed(AppServiceConnection sender, AppServiceClosedEventArgs args)
        {
            connection.ServiceClosed -= Connection_ServiceClosed;
            connection = null;
        }

        private async void OnRequestReceived(AppServiceConnection sender, AppServiceRequestReceivedEventArgs args)
        {
            if (connection == null) { 
                if (args.Request.Message.ContainsKey("data"))
                {
                    object message = null;
                    args.Request.Message.TryGetValue("data", out message);
                    MessageDialog dialog = new MessageDialog(message.ToString());
                    await dialog.ShowAsync();
                }
            }  
        }
    }
}
