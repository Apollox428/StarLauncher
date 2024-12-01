
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.konyaco.fluent.Colors
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.*
import com.konyaco.fluent.darkColors
import com.konyaco.fluent.gallery.window.WindowsWindowFrame
import com.konyaco.fluent.lightColors
import enums.Screen
import ui.nativelook.*
import ui.screen.HomeScreen
import ui.screen.SplashScreen
import ui.typography.CascadiaCode
import utils.AnsiRefactorer
import utils.parseColoredLog
import viewmodel.AppViewModel
import viewmodel.MainViewModel
import java.awt.SystemColor
import java.awt.Taskbar
import java.awt.Toolkit

@Composable
fun App(viewModel: MainViewModel) {

    val animSpec = slideInVertically { height -> height } + fadeIn() togetherWith
            slideOutVertically { height -> -height } + fadeOut(animationSpec = SpringSpec(stiffness = Spring.StiffnessMedium))

    ContentDialog(
        title = "Update available",
        visible = viewModel.shouldUpdate.value,
        size = DialogSize.Standard,
        primaryButtonText = "Update",
        closeButtonText = "Not now",
        onButtonClick = { viewModel.shouldUpdate.value = false },
        content = {
            Text("People will often ignore this message, however it is important to update every once in a while. Updates provide bug fixes and improvements in performance, plus new features may come by, with that said go check out the new update that is waiting for you.")
        }
    )

    AnimatedContent(
        targetState = viewModel.screen.value,
        transitionSpec = {
            animSpec.using(
                SizeTransform(clip = false)
            )
        }
    ) {
        when (it) {
            Screen.LOADING -> {
                SplashScreen(viewModel)
            }
            Screen.MAIN -> {
                HomeScreen(viewModel, viewModel.appViewModel!!)
            }
        }
    }
}

fun main() = application {
    val viewModel = remember { MainViewModel(this) }
    val appViewModel = remember { AppViewModel(viewModel) }
    viewModel.storePainter(painterResource("icon.svg"))
    viewModel.connectAppViewModel(appViewModel)

    var visible by remember { mutableStateOf(true) }

    val isSystemInDarkTheme = isSystemInDarkTheme()
    var preferredBackdropType by remember { mutableStateOf<WindowBackdrop>(WindowBackdrop.Mica(isSystemInDarkTheme)) }
    LaunchedEffect(isSystemInDarkTheme) {
        preferredBackdropType = preferredBackdropType.withTheme(isDarkTheme = isSystemInDarkTheme)
    }

    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenHeight = screenSize.height
    val screenWidth = screenSize.width
    println("Width: $screenWidth ; Height: $screenHeight")

    val state = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize((0.75 * screenSize.width.dp), (0.75 * screenSize.height.dp))
    )

    var colors by remember { mutableStateOf<Colors?>(null) }
    LaunchedEffect(isSystemInDarkTheme) {
        colors = if (isSystemInDarkTheme) darkColors(accent = Color(SystemColor.activeCaption.rgb)) else lightColors(accent = Color(SystemColor.activeCaption.rgb))
    }

    println("Red: " + SystemColor.activeCaption.red)
    println("Green: " + SystemColor.activeCaption.green)
    println("Blue: " + SystemColor.activeCaption.blue)

    val taskbar = Taskbar.getTaskbar()

    Window(
        onCloseRequest = ::exitApplication,
        title = viewModel.title,
        icon = viewModel.icon,
        state = state,
        visible = visible
    ) {
        viewModel.connectTaskbar(taskbar, this.window)

        WindowStyle(
            backdropType = preferredBackdropType
        )

        if (colors != null) {
            FluentTheme(colors = colors!!) {
                WindowsWindowFrame(
                    onCloseRequest = { exitApplication() },
                    icon = viewModel.icon!!,
                    title = viewModel.title,
                    backButtonVisible = viewModel.backButtonVisible.value,
                    backButtonClick = {  },
                    state = state
                ) {
                    App(viewModel)
                }
            }
        } else {
            FluentTheme {
                WindowsWindowFrame(
                    onCloseRequest = { exitApplication() },
                    icon = viewModel.icon!!,
                    title = viewModel.title,
                    backButtonVisible = viewModel.backButtonVisible.value,
                    backButtonClick = {  },
                    state = state
                ) {
                    App(viewModel)
                }
            }
        }
    }

    var diagWindow by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel.diagWindow.value) {
        diagWindow = viewModel.diagWindow.value
    }
    Window(title = "Console", visible = diagWindow, icon = viewModel.icon, onCloseRequest = { diagWindow = false }) {
        WindowStyle(
            backdropType = preferredBackdropType
        )
        val outputLines = appViewModel.clientOutput.value
        val consoleState = rememberScrollState()
        val textToDisplay = outputLines.joinToString("\n")
        FluentTheme(colors = colors ?: FluentTheme.colors) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(width = 1.dp, color = FluentTheme.colors.background.layer.alt, shape = RoundedCornerShape(10.dp))
                        .background(FluentTheme.colors.background.layer.default)
                        .padding(12.dp)
                ) {
                    val topFadeColor = animateColorAsState(
                        if (consoleState.value >= consoleState.maxValue) Color.Transparent else if (consoleState.value == 0) Color.Black else Color.Transparent
                    )
                    val bottomFadeColor = animateColorAsState(
                        if (consoleState.value >= consoleState.maxValue) Color.Black else Color.Transparent
                    )
                    SelectionContainer(
                        modifier = Modifier
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                val fadeColors = listOf(
                                    topFadeColor.value,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    bottomFadeColor.value
                                )
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(fadeColors),
                                    blendMode = BlendMode.DstIn
                                )
                            }
                    ) {
                        Text(parseColoredLog(AnsiRefactorer(textToDisplay)), fontFamily = CascadiaCode, color = Color.White, modifier = Modifier.fillMaxSize().verticalScroll(consoleState))
                    }
                }
                LaunchedEffect(textToDisplay) {
                    consoleState.animateScrollTo(textToDisplay.length)
                }
            }
        }


//        val menuState = rememberWindowState(
//            position = WindowPosition(Alignment.Center),
//            size = DpSize((0.25 * screenSize.width.dp), (0.25 * screenSize.height.dp))
//        )
//
//        Window(title = "ContextMenu", visible = true, icon = viewModel.icon, onCloseRequest = { diagWindow = false }, transparent = true, undecorated = true, focusable = false, alwaysOnTop = true, resizable = false, state = menuState) {
//            val isSysInDark = isSystemInDarkTheme()
//            WindowStyle(
//                backdropType = WindowBackdrop.AcrylicWithTint(Color.Transparent, true),
//                frameStyle = WindowFrameStyle(cornerPreference = WindowCornerPreference.ROUNDED)
//            )
//            WindowDraggableArea(Modifier.fillMaxSize()) {
//                Column(Modifier.fillMaxSize()) {
//                    ProgressBar()
//                    ProgressRing()
//                }
//            }
//        }





    }

}
