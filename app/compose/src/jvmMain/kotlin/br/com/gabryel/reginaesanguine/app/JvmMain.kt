package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.services.PreferencesStorage
import br.com.gabryel.reginaesanguine.app.services.ResPainterLoader
import br.com.gabryel.reginaesanguine.app.services.ResourcesResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.InteractionType.MOUSE
import java.awt.Toolkit
import kotlin.system.exitProcess

fun main() = application {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowState = rememberWindowState(size = DpSize(screenSize.width.dp, screenSize.height.dp))

    context(ResPainterLoader()) {
        Window(title = "Reginae Sanguine", state = windowState, onCloseRequest = { exitProcess(0) }) {
            val resourceLoader = remember { ResourcesResourceLoader() }
            val storage = remember { PreferencesStorage() }

            ReginaeSanguineTheme {
                CompositionLocalProvider(LocalDensity provides Density(2f), LocalInteractionType provides MOUSE) {
                    Scaffold { paddingValues ->
                        Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Center) {
                            App(
                                resourceLoader,
                                storage,
                                remember { storage.serverUrl.retrieve() ?: "http://10.0.2.2:8080" },
                            )
                        }
                    }
                }
            }
        }
    }
}
