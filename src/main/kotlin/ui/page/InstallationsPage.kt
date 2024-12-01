package ui.page

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.ScrollbarContainer
import com.konyaco.fluent.component.Text
import com.konyaco.fluent.component.rememberScrollbarAdapter
import com.konyaco.fluent.surface.Card
import data.parseDisplayName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import viewmodel.AppViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InstallationsPage(viewModel: AppViewModel) {
    Column(Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        val topFadeColor = animateColorAsState(
            if (scrollState.value >= scrollState.maxValue) Color.Transparent else if (scrollState.value == 0) Color.Black else Color.Transparent
        )
        val bottomFadeColor = animateColorAsState(
            if (scrollState.value >= scrollState.maxValue) Color.Black else Color.Transparent
        )
        ScrollbarContainer(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.weight(1f)
        ) {
            Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(32.dp)) {
                Text("Installations", style = FluentTheme.typography.title, color = Color(FluentTheme.colors.text.text.primary.value))
                Spacer(Modifier.height(16.dp))
                Text("Recently opened", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
                Spacer(Modifier.height(16.dp))
                Text("Your installations", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
                Spacer(Modifier.height(16.dp))
                val rowScrollState = rememberLazyListState()
                val rowStartFadeColor = animateColorAsState(
                    targetValue = if (!rowScrollState.canScrollBackward) Color.Black else Color.Transparent,
                    animationSpec = if (!rowScrollState.canScrollBackward) tween(10) else tween(25)
                )
                val rowEndFadeColor = animateColorAsState(
                    targetValue = if (!rowScrollState.canScrollForward) Color.Black else Color.Transparent,
                    animationSpec = if (!rowScrollState.canScrollForward) tween(10) else tween(25)
                )
                ScrollbarContainer(
                    adapter = rememberScrollbarAdapter(rowScrollState),
                    modifier = Modifier.fillMaxWidth(1f).height(96.dp),
                    isVertical = false
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                            .drawWithContent {
                                val fadeColors = listOf(
                                    rowStartFadeColor.value,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    Color.Black,
                                    rowEndFadeColor.value
                                )
                                drawContent()
                                drawRect(
                                    brush = Brush.horizontalGradient(fadeColors),
                                    blendMode = BlendMode.DstIn
                                )
                            },
                        state = rowScrollState
                    ) {
                        items(viewModel.launcherInstance.installations) { installation ->
                            Card(
                                onClick = {

                                },
                                modifier = Modifier
                                    .size(204.dp, 96.dp)

                            ) {
                                Text(installation.displayName)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Newly added", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
                Spacer(Modifier.height(16.dp))
                Text("Versions", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
                Spacer(Modifier.height(16.dp))
                val displayedVersions = remember { mutableStateOf(viewModel.launcherInstance.versions.filter { !it.isInstalled }) }
                Column(Modifier.fillMaxWidth().height(1024.dp)) {
                    val gridScrollState = rememberLazyGridState()
                    val gridTopFadeColor = animateColorAsState(
                        targetValue = if (!gridScrollState.canScrollBackward) Color.Black else Color.Transparent,
                        animationSpec = if (!gridScrollState.canScrollBackward) tween(10) else tween(25)
                    )
                    val gridBottomFadeColor = animateColorAsState(
                        targetValue = if (!gridScrollState.canScrollForward) Color.Black else Color.Transparent,
                        animationSpec = if (!gridScrollState.canScrollForward) tween(10) else tween(25)
                    )
                    ScrollbarContainer(
                        adapter = rememberScrollbarAdapter(gridScrollState),
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .weight(1f)
                                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                                .drawWithContent {
                                    val fadeColors = listOf(
                                        gridTopFadeColor.value,
                                        Color.Black,
                                        Color.Black,
                                        Color.Black,
                                        Color.Black,
                                        Color.Black,
                                        Color.Black,
                                        Color.Black,
                                        gridBottomFadeColor.value
                                    )
                                    drawContent()
                                    drawRect(
                                        brush = Brush.verticalGradient(fadeColors),
                                        blendMode = BlendMode.DstIn
                                    )
                                },
                            columns = GridCells.Adaptive(minSize = 172.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            state = gridScrollState
                        ) {
                            items(displayedVersions.value) { version ->
                                Card(
                                    onClick = {

                                    },
                                    modifier = Modifier
                                        .size(172.dp, 172.dp)

                                ) {
                                    Text(parseDisplayName(version))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}