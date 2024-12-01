package ui.nativelook.windows

import androidx.compose.ui.awt.ComposeWindow
import ui.nativelook.WindowBackdrop
import ui.nativelook.WindowCornerPreference
import ui.nativelook.windows.jna.Nt
import ui.nativelook.windows.jna.enums.DwmSystemBackdrop
import ui.nativelook.windows.jna.enums.DwmWindowCornerPreference
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.WinDef
import ui.nativelook.UnstableWindowBackdropApi
import java.awt.Window

internal val Window.hwnd
    get() =
        if (this is ComposeWindow) WinDef.HWND(Pointer(windowHandle))
        else WinDef.HWND(Native.getWindowPointer(this))

internal val windowsBuild by lazy {
    val osVersionInfo = Nt.getVersion()
    val buildNumber = osVersionInfo.buildNumber
    osVersionInfo.dispose()
    buildNumber
}

internal fun WindowBackdrop.toDwmSystemBackdrop(): DwmSystemBackdrop =
    when (this) {
        is WindowBackdrop.Mica -> DwmSystemBackdrop.DWMSBT_MAINWINDOW
        is WindowBackdrop.Acrylic, is WindowBackdrop.AcrylicWithTint -> DwmSystemBackdrop.DWMSBT_TRANSIENTWINDOW
        is WindowBackdrop.MicaTabbed -> DwmSystemBackdrop.DWMSBT_TABBEDWINDOW
        is WindowBackdrop.Solid, WindowBackdrop.None -> DwmSystemBackdrop.DWMSBT_DISABLE
    }

internal fun WindowCornerPreference.toDwmWindowCornerPreference(): DwmWindowCornerPreference =
    when (this) {
        WindowCornerPreference.DEFAULT -> DwmWindowCornerPreference.DWMWCP_DEFAULT
        WindowCornerPreference.NOT_ROUNDED -> DwmWindowCornerPreference.DWMWCP_DONOTROUND
        WindowCornerPreference.ROUNDED -> DwmWindowCornerPreference.DWMWCP_ROUND
        WindowCornerPreference.SMALL_ROUNDED -> DwmWindowCornerPreference.DWMWCP_ROUNDSMALL
    }