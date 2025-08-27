package br.com.gabryel.reginaesanguine.app

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat.getInsetsController
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import br.com.gabryel.reginaesanguine.app.services.AssetsResourceLoader
import br.com.gabryel.reginaesanguine.app.services.LocalInteractionType
import br.com.gabryel.reginaesanguine.app.services.ResCardImageLoader
import br.com.gabryel.reginaesanguine.app.ui.theme.ReginaeSanguineTheme
import br.com.gabryel.reginaesanguine.app.util.InteractionType.TOUCH

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        getInsetsController(window, window.decorView).hide(systemBars())

        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalInteractionType provides TOUCH) {
                context(ResCardImageLoader()) {
                    ReginaeSanguineTheme {
                        App(AssetsResourceLoader(this))
                    }
                }
            }
        }
    }
}
