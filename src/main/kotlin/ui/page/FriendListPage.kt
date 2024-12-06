package ui.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.component.Text
import viewmodel.AppViewModel

@Composable
fun FriendListPage(viewModel: AppViewModel) {
    Column(Modifier.fillMaxSize().padding(32.dp)) {
        Text("Friends", style = FluentTheme.typography.title, color = Color(FluentTheme.colors.text.text.primary.value))
    }
}