package ui.page

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.*
import com.konyaco.fluent.icons.Icons
import com.konyaco.fluent.icons.filled.Send
import com.konyaco.fluent.icons.regular.*
import com.konyaco.fluent.scheme.VisualState
import com.konyaco.fluent.scheme.collectVisualState
import com.konyaco.fluent.surface.Card
import data.getDisplayName
import data.parseDisplayName
import kotlinx.coroutines.launch
import ui.component.BetterTextField
import ui.nativelook.*
import viewmodel.AppViewModel
import java.awt.MouseInfo
import java.awt.Toolkit
import java.util.function.Predicate
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class, UnstableWindowBackdropApi::class)
@Composable
fun InstallationsPage(viewModel: AppViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val textColor = Color(FluentTheme.colors.text.text.primary.value)
    val installations = remember { viewModel.launcherInstance.installations }
    Column(Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        var scrollEnabled by remember { mutableStateOf(true) }
        ScrollbarContainer(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState, scrollEnabled)
//                    .draggable(
//                        orientation = Orientation.Vertical,
//                        state = rememberDraggableState { delta ->
//                            coroutineScope.launch {
//                                scrollState.scrollBy(-delta)
//                            }
//                        },
//                    )
                    .padding(32.dp)
            ) {
                Row(Modifier.fillMaxWidth()) {
                    Text("Installations", style = FluentTheme.typography.title, color = textColor)
                    Spacer(Modifier.weight(1f))
                    AccentButton(onClick = {}) {
                        Icon(Icons.Default.Add, null)
                        Text("Add new installation")
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Recently opened", style = FluentTheme.typography.subtitle, color = textColor)
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Your installations", style = FluentTheme.typography.subtitle, color = textColor)
                    Spacer(Modifier.weight(1f))
                    val interactionSource = remember { MutableInteractionSource() }
                    val visualState = interactionSource.collectVisualState(false)
                    val buttonArrowVisible = when (visualState) {
                        VisualState.Hovered, VisualState.Pressed -> true
                        else -> false
                    }
                    HyperlinkButton(onClick = {}, modifier = Modifier.animateContentSize(tween(if (buttonArrowVisible) 0 else 100)), interaction = interactionSource) {
                        Text("View all")
                        AnimatedVisibility(
                            visible = buttonArrowVisible,
                            enter = expandHorizontally() + fadeIn(),
                            exit = shrinkHorizontally(tween(75)) + fadeOut(tween(75))
                        ) {
                            Icon(Icons.Regular.ChevronRight, null)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth()) {
                    val rowScrollState = rememberLazyListState()
                    val canScrollBackward by remember {
                        derivedStateOf {
                            !rowScrollState.canScrollBackward
                        }
                    }
                    val canScrollForward by remember {
                        derivedStateOf {
                            !rowScrollState.canScrollForward
                        }
                    }
                    val rowStartFadeColor = animateColorAsState(
                        targetValue = if (canScrollBackward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollBackward) tween(10) else tween(25)
                    )
                    val rowEndFadeColor = animateColorAsState(
                        targetValue = if (canScrollForward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollForward) tween(10) else tween(25)
                    )
                    var scrollToRow by remember { mutableStateOf(0) }
                    LaunchedEffect(scrollToRow) {
                        rowScrollState.animateScrollToItem(scrollToRow)
                    }
                    ScrollbarContainer(
                        adapter = rememberScrollbarAdapter(rowScrollState),
                        modifier = Modifier.fillMaxWidth(1f).height(96.dp),
                        isVertical = false
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        coroutineScope.launch {
                                            rowScrollState.scrollBy(-delta)
                                        }
                                    },
                                )
                                .onPointerEvent(PointerEventType.Scroll) {
                                    val scrollDeltaY = rowScrollState.firstVisibleItemIndex + -it.changes.first().scrollDelta.y.roundToInt()
                                    if (scrollDeltaY > 0) scrollToRow = if (scrollToRow == 1 && scrollDeltaY == 1 && it.changes.first().scrollDelta.y.roundToInt() == 1) 0 else scrollDeltaY
                                }
                                .onPointerEvent(PointerEventType.Enter) {
                                    scrollEnabled = false
                                }
                                .onPointerEvent(PointerEventType.Exit) {
                                    scrollEnabled = true
                                }
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
                            state = rowScrollState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                installations,
                                key = { installation ->
                                    installation.id
                                }
                            ) { installation ->
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
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Newly added", style = FluentTheme.typography.subtitle, color = textColor)
                    Spacer(Modifier.weight(1f))
                    val interactionSource = remember { MutableInteractionSource() }
                    val visualState = interactionSource.collectVisualState(false)
                    val buttonArrowVisible = when (visualState) {
                        VisualState.Hovered, VisualState.Pressed -> true
                        else -> false
                    }
                    HyperlinkButton(onClick = {}, modifier = Modifier.animateContentSize(tween(if (buttonArrowVisible) 0 else 100)), interaction = interactionSource) {
                        Text("View more")
                        AnimatedVisibility(
                            visible = buttonArrowVisible,
                            enter = fadeIn() + expandHorizontally(),
                            exit = fadeOut(tween(75)) + shrinkHorizontally(tween(75))
                        ) {
                            Icon(Icons.Regular.ChevronRight, null)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Box(Modifier.fillMaxWidth()) {
                    val newlyAddedVersions = remember { viewModel.launcherInstance.versions.take(10) }
                    val rowScrollState = rememberLazyListState()
                    val canScrollBackward by remember {
                        derivedStateOf {
                            !rowScrollState.canScrollBackward
                        }
                    }
                    val canScrollForward by remember {
                        derivedStateOf {
                            !rowScrollState.canScrollForward
                        }
                    }
                    val rowStartFadeColor = animateColorAsState(
                        targetValue = if (canScrollBackward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollBackward) tween(10) else tween(25)
                    )
                    val rowEndFadeColor = animateColorAsState(
                        targetValue = if (canScrollForward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollForward) tween(10) else tween(25)
                    )
                    var scrollToRow by remember { mutableStateOf(0) }
                    LaunchedEffect(scrollToRow) {
                        rowScrollState.animateScrollToItem(scrollToRow)
                    }
                    ScrollbarContainer(
                        adapter = rememberScrollbarAdapter(rowScrollState),
                        modifier = Modifier.fillMaxWidth(1f).height(96.dp),
                        isVertical = false
                    ) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .draggable(
                                    orientation = Orientation.Horizontal,
                                    state = rememberDraggableState { delta ->
                                        coroutineScope.launch {
                                            rowScrollState.scrollBy(-delta)
                                        }
                                    },
                                )
                                .onPointerEvent(PointerEventType.Scroll) {
                                    val scrollDeltaY = rowScrollState.firstVisibleItemIndex + -it.changes.first().scrollDelta.y.roundToInt()
                                    if (scrollDeltaY > 0) scrollToRow = if (scrollToRow == 1 && scrollDeltaY == 1 && it.changes.first().scrollDelta.y.roundToInt() == 1) 0 else scrollDeltaY
                                }
                                .onPointerEvent(PointerEventType.Enter) {
                                    scrollEnabled = false
                                }
                                .onPointerEvent(PointerEventType.Exit) {
                                    scrollEnabled = true
                                }
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
                            state = rowScrollState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = newlyAddedVersions,
                                key = { version ->
                                    version.identifier
                                }
                            ) { version ->
                                Card(
                                    onClick = {

                                    },
                                    modifier = Modifier
                                        .size(204.dp, 96.dp)

                                ) {
                                    Text(version.getDisplayName())
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    Text("Versions", style = FluentTheme.typography.subtitle, color = textColor)
                    Spacer(Modifier.weight(1f))
                    var value by remember { mutableStateOf(TextFieldValue()) }
                    BetterTextField(value, onValueChange = { value = it }, trailingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.width(256.dp), singleLine = true, placeholder = { Text("Search versions...") })
                    Spacer(Modifier.width(8.dp))

                    MenuFlyoutContainer(
                        flyout = {
                            MenuFlyoutItem(
                                text = { Text("Send") },
                                onClick = { isFlyoutVisible = false },
                                icon = { Icon(Icons.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp)) })
                            MenuFlyoutItem(
                                text = { Text("Reply") },
                                onClick = { isFlyoutVisible = false },
                                icon = { Icon(Icons.Default.MailArrowDoubleBack, contentDescription = "Reply", modifier = Modifier.size(20.dp)) })
                            MenuFlyoutItem(
                                text = { Text("Reply All") },
                                onClick = { isFlyoutVisible = false },
                                icon = { Icon(Icons.Default.MailArrowDoubleBack, contentDescription = "Reply All", modifier = Modifier.size(20.dp)) })
                        },
                        content = {
                            DropDownButton(
                                onClick = { isFlyoutVisible = !isFlyoutVisible },
                                content = { Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(24.dp)) }
                            )
                        },
                        adaptivePlacement = true,
                        placement = FlyoutPlacement.Auto
                    )

                }
                Spacer(Modifier.height(16.dp))
                val displayedVersions = remember { viewModel.launcherInstance.versions.filter { !it.isInstalled } }
                Column(Modifier.fillMaxWidth().height(1024.dp)) {
                    val gridScrollState = rememberLazyGridState()
                    val canScrollBackward by remember {
                        derivedStateOf {
                            !gridScrollState.canScrollBackward
                        }
                    }
                    val canScrollForward by remember {
                        derivedStateOf {
                            !gridScrollState.canScrollForward
                        }
                    }
                    val gridTopFadeColor = animateColorAsState(
                        targetValue = if (canScrollBackward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollBackward) tween(10) else tween(25)
                    )
                    val gridBottomFadeColor = animateColorAsState(
                        targetValue = if (canScrollForward) Color.Black else Color.Transparent,
                        animationSpec = if (canScrollForward) tween(10) else tween(25)
                    )
                    ScrollbarContainer(
                        adapter = rememberScrollbarAdapter(gridScrollState),
                        modifier = Modifier.weight(1f)
                    ) {
                        LazyVerticalGrid(
                            modifier = Modifier
                                .weight(1f)
//                                .draggable(
//                                    orientation = Orientation.Vertical,
//                                    state = rememberDraggableState { delta ->
//                                        coroutineScope.launch {
//                                            gridScrollState.scrollBy(-delta)
//                                        }
//                                    },
//                                )
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
                            items(
                                items = displayedVersions,
                                key = { version ->
                                    version.identifier
                                }
                            ) { version ->
                                Card(
                                    onClick = {

                                    },
                                    modifier = Modifier
                                        .size(172.dp, 172.dp)

                                ) {
                                    Text(version.friendlyName)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}