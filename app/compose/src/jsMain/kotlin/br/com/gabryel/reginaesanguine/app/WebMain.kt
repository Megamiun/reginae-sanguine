package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import br.com.gabryel.reginaesanguine.app.service.WebResourceLoader
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.services.ResPainterLoader
import br.com.gabryel.reginaesanguine.app.storage.LocalStorage
import br.com.gabryel.reginaesanguine.app.ui.theme.Background
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.InteractionType.MOUSE
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport("content") {
            val resourceLoader = remember { WebResourceLoader() }
            val storage = remember { LocalStorage() }

            ReginaeSanguineTheme {
                CompositionLocalProvider(LocalDensity provides Density(2f), LocalInteractionType provides MOUSE) {
                    Box(Modifier.fillMaxSize().background(Background), contentAlignment = TopCenter) {
                        Box(Modifier.size(1000.dp, 500.dp), contentAlignment = Center) {
                            context(ResPainterLoader()) {
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
}
