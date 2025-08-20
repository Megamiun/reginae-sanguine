package br.com.gabryel.reginaesanguine.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import br.com.gabryel.reginaesanguine.app.service.WebResourceLoader
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.App
import br.com.gabryel.reginaesanguine.app.ui.theme.PurpleLight
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        ComposeViewport("content") {
            context(ResCardImageLoader()) {
                ReginaeSanguineTheme {
                    Box(Modifier.fillMaxSize().background(PurpleLight), contentAlignment = TopCenter) {
                        Box(Modifier.size(1000.dp, 500.dp), contentAlignment = Center) {
                            App(WebResourceLoader())
                        }
                    }
                }
            }
        }
    }
}
