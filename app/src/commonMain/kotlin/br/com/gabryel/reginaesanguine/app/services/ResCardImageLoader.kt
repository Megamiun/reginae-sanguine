package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.app.generated.resources.Res
import br.com.gabryel.app.generated.resources.allDrawableResources
import br.com.gabryel.reginaesanguine.app.Card
import br.com.gabryel.reginaesanguine.app.util.Logger
import coil3.compose.rememberAsyncImagePainter
import org.jetbrains.compose.resources.painterResource

class ResCardImageLoader(private val useComposePainter: Boolean = false) : CardImageLoader {
    private val logger = Logger(this::class)

    @Composable
    override fun loadCardImage(card: Card): Painter? = runCatching {
        val resource = Res.allDrawableResources[card.id]
            ?: return null

        if (useComposePainter) {
            painterResource(resource)
        } else {
            val uri = Res.getUri("drawable/${card.id}.${card.ext}")
            rememberAsyncImagePainter(uri)
        }
    }.onFailure { err ->
        logger.error("Couldn't load card at ${card.id}", err)
    }.getOrNull()
}
