package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.allDrawableResources
import br.com.gabryel.reginaesanguine.app.util.Logger
import coil3.compose.rememberAsyncImagePainter

class ResCardImageLoader : CardImageLoader {
    private val logger = Logger(this::class)

    @Composable
    override fun loadCardImage(pack: String, id: String): Painter? {
        val name = "packs_queens_blood_assets_$id".lowercase()
        Res.allDrawableResources[name] ?: return null

        return runCatching {
            val resUri = Res.getUri("drawable/$name.png")
            rememberAsyncImagePainter(
                resUri,
                onError = { logger.error("Couldn't load card at $name", it.result.throwable) },
            )
        }.onFailure { err ->
            logger.error("Couldn't load card at $name", err)
        }.getOrNull()
    }
}
