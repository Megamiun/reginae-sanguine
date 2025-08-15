package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.reginaesanguine.domain.PlayerPosition

fun interface CardImageLoader {
    @Composable
    fun loadCardImage(pack: String, id: String): Painter?
}
