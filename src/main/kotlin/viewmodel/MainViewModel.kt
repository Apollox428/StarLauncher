package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import enums.Screen
import java.awt.Taskbar
import java.awt.Window

class MainViewModel(val applicationScope: ApplicationScope) {

    var title = "DreamLauncher"
    var screen = mutableStateOf(Screen.LOADING)
    var icon: Painter? = null
    val backButtonVisible = mutableStateOf(false)

    val loadingText = mutableStateOf("  ")
    val splashTexts = listOf(
        "Did you know that this was originally going to be called \"Grapchis Calculator\"?",
        "This was heavily inspired in the \"Animation vs Math\" video made by Alan Becker. Go check it out!",
        "Math is like sheet music, everyone can read it but not everyone can hear it. Can you hear the music?",
        "√1 2^3 Σ π ...and it was delicious.",
        "The π is a lie.",
        "Math visualized.",
        "Should I make this a game?",
        "Math: The Game"
    )

    val shouldUpdate = mutableStateOf(false)
    val diagWindow = mutableStateOf(false)

    var appViewModel: AppViewModel? = null
    var taskbar: Taskbar? = null
    var window: Window? = null

    init {  }

    fun storePainter(painterResource: Painter) {
        icon = painterResource
    }

    fun connectAppViewModel(viewModel: AppViewModel) {
        appViewModel = viewModel
    }

    fun connectTaskbar(incomingTaskbar: Taskbar, incomingWindow: Window) {
        window = incomingWindow
        taskbar = incomingTaskbar
//        taskbar!!.requestWindowUserAttention(window!!)
//        incomingTaskbar.setWindowProgressState(window!!, Taskbar.State.INDETERMINATE);
    }

}