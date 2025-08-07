package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.app.generated.resources.Res
import br.com.gabryel.app.generated.resources.allDrawableResources
import br.com.gabryel.reginaesanguine.app.util.Logger
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import org.jetbrains.compose.resources.painterResource

class ResCardImageLoader : CardImageLoader {
    private val logger = Logger(this::class)

    @Composable
    override fun loadCardImage(pack: String, id: String): Painter? {
        val name = "queens_blood_base_$id".lowercase()
        val resource = Res.allDrawableResources[name] ?: return null

        return runCatching {
            painterResource(resource)
        }.onFailure { err ->
            logger.error("Couldn't load card at $name", err)
        }.getOrNull()
    }
}
