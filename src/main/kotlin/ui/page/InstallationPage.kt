package ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.ScrollbarContainer
import com.konyaco.fluent.component.Text
import com.konyaco.fluent.component.rememberScrollbarAdapter
import viewmodel.AppViewModel

@Composable
fun InstallationPage(viewModel: AppViewModel) {

    val installation = viewModel.currentInstallation.value!!

    Column(Modifier.fillMaxSize().padding(32.dp)) {

        Text(installation.displayName, style = FluentTheme.typography.title, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Description", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Recent presets", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Quick tips", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Community creations", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
    }
}