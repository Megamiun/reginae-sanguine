package br.com.gabryel.reginaesanguine.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.services.NSBundleResourceLoader
import br.com.gabryel.reginaesanguine.app.services.ResPainterLoader
import br.com.gabryel.reginaesanguine.app.services.UserDefaultsStorage
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.InteractionType.TOUCH
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController = ComposeUIViewController {
    val resourceLoader = remember { NSBundleResourceLoader() }
    val storage = remember { UserDefaultsStorage() }

    CompositionLocalProvider(LocalInteractionType provides TOUCH) {
        context(ResPainterLoader()) {
            ReginaeSanguineTheme {
                App(resourceLoader, storage, remember { storage.serverUrl.retrieve() ?: "http://localhost:8080" })
            }
        }
    }
}
