import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlin.system.exitProcess

val PurpleLight = Color(63, 48, 60)

fun main() = application {
    Window(title = "Reginae Sanguine", onCloseRequest = { exitProcess(0) }) {
        Scaffold { paddingValues ->
            Box(Modifier.fillMaxSize().background(PurpleLight).padding(paddingValues), contentAlignment = Center) {
            }
        }
    }
}
