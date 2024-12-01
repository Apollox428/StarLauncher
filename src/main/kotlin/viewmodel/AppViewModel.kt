package viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.alexfacciorusso.windowsregistryktx.main
import data.Installation
import data.Version
import enums.Page
import enums.Screen
import kotlinx.coroutines.*
import utils.Launcher
import java.sql.Time

class AppViewModel(mainViewModel: MainViewModel) {

    val launcherInstance = Launcher(mainViewModel, this)

    var clientOutput: MutableState<List<String>> = mutableStateOf(emptyList())
    val page = mutableStateOf(Page.HOME)
    //val pageExtra = mutableStateOf("")
    val currentInstallation = mutableStateOf<Installation?>(null)

    init {
        CoroutineScope(Dispatchers.Default).launch {
            launcherInstance.loadVersions()
            mainViewModel.screen.value = Screen.MAIN
        }
    }

    fun setPage(destPage: Page, extra: String = "") {
        page.value = destPage
        //pageExtra.value = extra
    }

    fun parseTimestamp(timestamp: Long): String {
        val currentHour = Time(timestamp).toLocalTime().hour

        return when (currentHour) {
            in 6..11 -> {
                "Good morning"
            }
            in 12..18 -> {
                "Good afternoon"
            }
            else -> {
                "Good night"
            }
        }
    }

}