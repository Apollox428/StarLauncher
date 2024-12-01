package jna.windows.structure

object WinUserConst {

    //calculate non client area size message
    const val WM_NCCALCSIZE = 0x0083
    // non client area hit test message
    const val WM_NCHITTEST = 0x0084
    // mouse move message
    const val WM_MOUSEMOVE = 0x0200
    // left mouse button down message
    const val WM_LBUTTONDOWN = 0x0201
    // left mouse button up message
    const val WM_LBUTTONUP = 0x0202
    // non client area mouse move message
    const val WM_NCMOUSEMOVE = 0x00A0
    // non client area left mouse down message
    const val WM_NCLBUTTONDOWN = 0x00A1
    // non client area left mouse up message
    const val WM_NCLBUTTONUP = 0x00A2

    /**
     * [WM_NCHITTEST] Mouse Position Codes
     */
    // pass the hit test to parent window
    internal const val HTTRANSPANRENT = -1
    // no hit test
    internal const val HTNOWHERE = 0
    // client area
    internal const val HTCLIENT = 1
    // title bar
    internal const val HTCAPTION = 2
    // max button
    internal const val HTMAXBUTTON = 9
    // window edges
    internal const val HTLEFT = 10
    internal const val HTRIGHT = 11
    internal const val HTTOP = 12
    internal const val HTTOPLEFT = 13
    internal const val HTTOPRIGHT = 14
    internal const val HTBOTTOM = 15
    internal const val HTBOTTOMLEFT = 16
    internal const val HTBOTTOMRIGHT = 17
}