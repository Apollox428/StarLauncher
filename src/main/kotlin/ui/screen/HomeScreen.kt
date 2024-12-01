package ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konyaco.fluent.ExperimentalFluentApi
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.animation.FluentDuration
import com.konyaco.fluent.animation.FluentEasing
import com.konyaco.fluent.component.Icon
import com.konyaco.fluent.component.SideNav
import com.konyaco.fluent.component.SideNavItem
import com.konyaco.fluent.component.Text
import com.konyaco.fluent.icons.Icons
import com.konyaco.fluent.icons.regular.*
import data.Installation
import enums.Page
import ui.page.*
import viewmodel.AppViewModel
import viewmodel.MainViewModel
import javax.net.ServerSocketFactory

@OptIn(ExperimentalFluentApi::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, appViewModel: AppViewModel) {
    val animSpec = slideInVertically { height -> height } + fadeIn() togetherWith
            slideOutVertically { height -> -height } + fadeOut(animationSpec = SpringSpec(stiffness = Spring.StiffnessMedium))
    //Hi Phi!
    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.fillMaxHeight().width(46.62.dp))
        Column(modifier = Modifier.offset(x = 1.618.dp, y = 1.618.dp).clip(RoundedCornerShape(topStart = 10.dp)).fillMaxHeight().weight(1f).border(width = 1.dp, color = FluentTheme.colors.background.layer.alt, shape = RoundedCornerShape(topStart = 10.dp)).background(FluentTheme.colors.background.layer.default)) {
            if (viewModel.appViewModel != null) {
                AnimatedContent(
                    targetState = viewModel.appViewModel!!.page.value,
                    transitionSpec = {
                        animSpec.using(
                            SizeTransform(clip = false)
                        )
                    }
                ) {
                    when (it) {
                        Page.HOME -> {
                            HomePage(viewModel.appViewModel!!)
                        }
                        Page.INSTALLATIONS -> {
                            InstallationsPage(viewModel.appViewModel!!)
                        }
                        Page.INSTALLATION -> {
                            InstallationPage(viewModel.appViewModel!!)
                        }
                        Page.SERVERS -> {
                            ServersPage(viewModel.appViewModel!!)
                        }
                        Page.SERVER -> {
                            ServerPage(viewModel.appViewModel!!)
                        }
                        Page.FRIEND_LIST -> {
                            FriendListPage(viewModel.appViewModel!!)
                        }
                        Page.FRIEND -> {
                            FriendPage(viewModel.appViewModel!!)
                        }
                        Page.ACCOUNT -> {
                            AccountPage(viewModel.appViewModel!!)
                        }
                        Page.SETTINGS -> {
                            SettingsPage(viewModel.appViewModel!!)
                        }
                    }
                }
            } else {
                Text("Something went wrong.", style = FluentTheme.typography.title, color = Color(FluentTheme.colors.text.text.primary.value))
            }
        }
    }

    var expanded by remember { mutableStateOf(false) }
    var friendsExpanded by remember { mutableStateOf(false) }
    var installationsExpanded by remember { mutableStateOf(false) }
    var serversExpanded by remember { mutableStateOf(false) }
    var selItem by remember { mutableStateOf("Home") }
    val animAcrylicColor = animateColorAsState(
        if (expanded) FluentTheme.colors.background.acrylic.default else FluentTheme.colors.background.acrylic.default.copy(alpha = 0f)
    )
    val animBorderColor = animateColorAsState(
        if (expanded) FluentTheme.colors.background.layer.alt else FluentTheme.colors.background.layer.alt.copy(alpha = 0f)
    )
    SideNav(
        modifier = Modifier
            .offset(x = -(1.618).dp, y = 1.618.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
            .border(width = 1.dp, color = animBorderColor.value, RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
            .background(animAcrylicColor.value)
            .padding(end = 4.dp)
            .offset(x = 1.618.dp, y = -(1.618).dp),
        expanded = expanded,
        onExpandStateChange = { expanded = it },
        footer = {
            SideNavItem(
                icon = { Icon(imageVector = Icons.Regular.Person, contentDescription = "Account") },
                content = { Text("Account") },
                onClick = {
                    appViewModel.setPage(Page.ACCOUNT)
                    selItem = "Account"
                    expanded = false
                },
                selected = selItem == "Account"
            )
            SideNavItem(
                icon = { Icon(imageVector = Icons.Regular.Settings, contentDescription = "Settings") },
                content = { Text("Settings") },
                onClick = {
                    appViewModel.setPage(Page.SETTINGS)
                    selItem = "Settings"
                    expanded = false
                },
                selected = selItem == "Settings"
            )
        }
    ) {
        SideNavItem(
            icon = { Icon(imageVector = Icons.Regular.Home, contentDescription = "Settings") },
            content = { Text("Home") },
            onClick = {
                appViewModel.setPage(Page.HOME)
                selItem = "Home"
                expanded = false
            },
            selected = selItem == "Home"
        )
        SideNavItem(
            icon = { Icon(imageVector = Icons.Regular.Apps, contentDescription = "Installations") },
            content = { Text("Installations") },
            onClick = {
                appViewModel.setPage(Page.INSTALLATIONS)
                selItem = "Installations"
                serversExpanded = !serversExpanded
            },
            selected = selItem == "Installations",
            expandItems = serversExpanded,
            items = {
                for (installation in viewModel.appViewModel!!.launcherInstance.installations) {
                    SideNavItem(
                        icon = { Icon(imageVector = Icons.Regular.Cube, contentDescription = installation.displayName) },
                        content = { Text(installation.displayName) },
                        onClick = {
                            appViewModel.currentInstallation.value = installation
                            appViewModel.setPage(Page.INSTALLATION, installation.id)
                            selItem = installation.id
                            expanded = false
                        },
                        selected = selItem == installation.id
                    )
                }
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Add, contentDescription = "Add new installation") },
                    content = { Text("Add new installation") },
                    onClick = {
                        expanded = false
                    },
                    selected = false
                )
            }
        )
        SideNavItem(
            icon = { Icon(imageVector = Icons.Regular.Server, contentDescription = "Servers") },
            content = { Text("Servers") },
            onClick = {
                appViewModel.setPage(Page.SERVER)
                selItem = "Servers"
                installationsExpanded = !installationsExpanded
            },
            selected = selItem == "Servers",
            expandItems = installationsExpanded,
            items = {
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Storage, contentDescription = "Settings") },
                    content = { Text("Purpur 1.21") },
                    onClick = {
                        selItem = "server-83d3da3"
                    },
                    selected = selItem == "server-83d3da3"
                )
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Storage, contentDescription = "Settings") },
                    content = { Text("Survival") },
                    onClick = {
                        selItem = "server-93jdgjf9"
                    },
                    selected = selItem == "server-93jdgjf9"
                )
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Storage, contentDescription = "Settings") },
                    content = { Text("Fabric 1.8.9") },
                    onClick = {
                        selItem = "server-4hrf4h7"
                    },
                    selected = selItem == "server-4hrf4h7"
                )
            }
        )
        SideNavItem(
            icon = { Icon(imageVector = Icons.Regular.People, contentDescription = "Friends") },
            content = { Text("Friends") },
            onClick = {
                appViewModel.setPage(Page.FRIEND_LIST)
                selItem = "Friends"
                friendsExpanded = !friendsExpanded
            },
            selected = selItem == "Friends",
            expandItems = friendsExpanded,
            items = {
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Person, contentDescription = "Settings") },
                    content = { Text("Friend 1") },
                    onClick = {
                        selItem = "Friend 1"
                    },
                    selected = selItem == "Friend 1"
                )
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Person, contentDescription = "Settings") },
                    content = { Text("Friend 2") },
                    onClick = {
                        selItem = "Friend 2"
                    },
                    selected = selItem == "Friend 2"
                )
                SideNavItem(
                    icon = { Icon(imageVector = Icons.Regular.Person, contentDescription = "Settings") },
                    content = { Text("Friend 3") },
                    onClick = {
                        selItem = "Friend 3"
                    },
                    selected = selItem == "Friend 3"
                )
            }
        )
    }
}