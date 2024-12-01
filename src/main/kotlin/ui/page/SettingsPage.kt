package ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.Text
import viewmodel.AppViewModel

@Composable
fun SettingsPage(viewModel: AppViewModel) {
    Column(Modifier.fillMaxSize().padding(32.dp)) {
        Text(viewModel.parseTimestamp(System.currentTimeMillis()), style = FluentTheme.typography.title, color = Color(
            FluentTheme.colors.text.text.primary.value)
        )
        Spacer(Modifier.height(16.dp))
        Text("Recently opened", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Recent presets", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Quick tips", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
        Spacer(Modifier.height(16.dp))
        Text("Community creations", style = FluentTheme.typography.subtitle, color = Color(FluentTheme.colors.text.text.primary.value))
    }
}