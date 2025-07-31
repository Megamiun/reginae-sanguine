package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.app.generated.resources.Res
import br.com.gabryel.reginaesanguine.app.util.Logger
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import coil3.compose.rememberAsyncImagePainter

class ResResourceLoader : ResourceLoader {
    private val logger = Logger(this.javaClass)

    @Composable
    override fun loadCardImage(pack: String, player: PlayerPosition, id: String): Painter? {
        val color = when (player) {
            LEFT -> "blue"
            RIGHT -> "red"
        }

        val path = "drawable/${pack}_${color}_$id.png"
        return runCatching {
            val uri = Res.getUri(path)
            rememberAsyncImagePainter(
                uri,
                onError = { logger.error("Error loading $path", it.result.throwable) },
            )
        }.onFailure { err ->
            logger.error("Couldn't load card at $path", err)
        }.getOrNull()
    }
}
