package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil3.transform.Transformation
import org.jetbrains.compose.resources.DrawableResource

interface PainterLoader {
    @Composable
    fun loadCardImage(pack: String, id: String): Painter?

    @Composable
    fun loadStaticImage(id: DrawableResource, transformations: List<Transformation> = emptyList()): Painter
}
