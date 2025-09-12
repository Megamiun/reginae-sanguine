package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.reginaesanguine.app.Res
import br.com.gabryel.reginaesanguine.app.allDrawableResources
import br.com.gabryel.reginaesanguine.app.util.Logger
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import org.jetbrains.compose.resources.DrawableResource

class ResPainterLoader : PainterLoader {
    private val logger = Logger(this::class)

    @Composable
    override fun loadCardImage(pack: String, id: String): Painter? {
        val name = "packs_queens_blood_assets_$id".lowercase()
        Res.allDrawableResources[name] ?: return null

        return loadRes(name)
    }

    @Composable
    override fun loadStaticImage(id: DrawableResource, transformations: List<Transformation>): Painter {
        val name = Res.allDrawableResources.filterValues { value -> value == id }.keys.first()

        return loadRes(name, transformations)
            ?: error("Static Drawable could not be loaded. Will fail.")
    }

    @Composable
    private fun loadRes(name: String, transformations: List<Transformation> = emptyList()): AsyncImagePainter? = runCatching {
        val resUri = Res.getUri("drawable/$name.png")

        val request = ImageRequest.Builder(LocalPlatformContext.current)
            .data(resUri)
            .transformations(transformations)
            .build()

        rememberAsyncImagePainter(
            request,
            onError = { logger.error("Couldn't load card at $name", it.result.throwable) },
        )
    }.onFailure { err ->
        logger.error("Couldn't load card at $name", err)
    }.getOrNull()
}
