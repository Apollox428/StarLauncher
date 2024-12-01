package ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.ProgressBar
import com.konyaco.fluent.component.Text
import com.konyaco.fluent.icons.Icons
import com.konyaco.fluent.icons.regular.DocumentError
import kotlinx.coroutines.delay
import ui.typography.SegoeText
import viewmodel.MainViewModel
import java.awt.SystemColor
import kotlin.random.Random

@Composable
fun SplashScreen(viewModel: MainViewModel) {

    val animSpec = slideInVertically { height -> height } + fadeIn() togetherWith
            slideOutVertically { height -> -height } + fadeOut(animationSpec = SpringSpec(stiffness = Spring.StiffnessMedium))

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            viewModel.loadingText.value = viewModel.splashTexts[Random.nextInt(viewModel.splashTexts.size)]
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (viewModel.icon != null) {
                Image(viewModel.icon!!, null, Modifier.size(128.dp))
            } else {
                Image(Icons.Default.DocumentError, null, Modifier.size(128.dp))
            }
            Spacer(Modifier.height(48.dp))
            ProgressBar(color = Color(SystemColor.activeCaption.rgb))
        }
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = viewModel.loadingText.value,
            transitionSpec = {
                animSpec.using(
                    SizeTransform(clip = false)
                )
            }
        ) {
            if (it.isNotEmpty()) {
                Text(it, modifier = Modifier.fillMaxWidth(0.35f), fontFamily = SegoeText, fontSize = 12.sp, color = Color(FluentTheme.colors.text.text.primary.value), textAlign = TextAlign.Center)
            }
        }
        Spacer(Modifier.height(64.dp))
    }
}