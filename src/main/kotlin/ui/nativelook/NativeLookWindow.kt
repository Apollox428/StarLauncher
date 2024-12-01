/*
* MIT License

Copyright (c) 2023 All contributors
Copyright (c) 2022 MayakaApps

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*
* Took from alexfacciorusso/ComposeWindowStyler in GitHub.
*
* */
package ui.nativelook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.window.*

interface NativeLookWindowScope : FrameWindowScope {
    val appliedBackdrop: WindowBackdrop
}

private data class NativeLookWindowScopeImpl(
    override val appliedBackdrop: WindowBackdrop,
    private val frameWindowScope: FrameWindowScope,
) : NativeLookWindowScope, FrameWindowScope by frameWindowScope

@Composable
fun WindowScope.WindowStyle(
    backdropType: WindowBackdrop = WindowBackdrop.None,
    frameStyle: WindowFrameStyle = WindowFrameStyle()
) {

    val manager = remember {
        WindowStyleManager(
            window as ComposeWindow,
            backdropType,
            frameStyle,
        )
    }

    var appliedBackdrop by remember {
        mutableStateOf<WindowBackdrop>(WindowBackdrop.None)
    }

    LaunchedEffect(backdropType) {
        manager.preferredBackdrop = backdropType
    }

    LaunchedEffect(backdropType) {
        // TODO: to explore if manager can be totally removed
        appliedBackdrop = manager.apply()
    }

}
