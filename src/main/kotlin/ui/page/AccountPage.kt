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
fun AccountPage(viewModel: AppViewModel) {
    val textColor = Color(FluentTheme.colors.text.text.primary.value)
    Column(Modifier.fillMaxSize().padding(32.dp)) {
        Text("Account", style = FluentTheme.typography.title, color = textColor)
        Spacer(Modifier.height(16.dp))
        Text("Recently opened", style = FluentTheme.typography.subtitle, color = textColor)
        Spacer(Modifier.height(16.dp))
        Text("Recent presets", style = FluentTheme.typography.subtitle, color = textColor)
        Spacer(Modifier.height(16.dp))
        Text("Quick tips", style = FluentTheme.typography.subtitle, color = textColor)
        Spacer(Modifier.height(16.dp))
        Text("Community creations", style = FluentTheme.typography.subtitle, color = textColor)
    }
}