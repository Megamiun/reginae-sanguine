package br.com.gabryel.reginaesanguine.app

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.services.ResourcesResourceLoader
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.InteractionType.TOUCH
import platform.UIKit.UIViewController

fun mainViewController(): UIViewController = ComposeUIViewController {
    CompositionLocalProvider(LocalInteractionType provides TOUCH) {
        context(ResCardImageLoader()) {
            ReginaeSanguineTheme {
                App(ResourcesResourceLoader())
            }
        }
    }
}
