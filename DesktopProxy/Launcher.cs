using System.Diagnostics;

namespace DesktopProxy
{
    internal class Launcher
    {
        public void Test(string command, string[] args, DataReceivedEventHandler outputDataReceived) {
            StartProcess(command, args, outputDataReceived);
        }
        private void StartProcess(string command, string[] args, DataReceivedEventHandler outputDataReceived)
        {
            Process process = new Process();
            process.StartInfo.FileName = @"powershell";
            process.StartInfo.Arguments = "/c \"irm https://github.com/scottmckendry/ps-color-scripts/raw/refs/heads/main/ps-color-scripts/colorscripts/hex.ps1 | iex\"";
            process.StartInfo.UseShellExecute = false;
            process.StartInfo.RedirectStandardOutput = true;
            process.StartInfo.RedirectStandardError = true;
            process.StartInfo.CreateNoWindow = true;
            process.OutputDataReceived += outputDataReceived;
            process.ErrorDataReceived += outputDataReceived;
            process.Start();
            process.BeginOutputReadLine();
            process.BeginErrorReadLine();
        }

        /*private void p_OutputDataReceived(object sender, DataReceivedEventArgs data) {
            Debug.WriteLine(data.Data);
        }*/
    }
}
