package br.com.gabryel.reginaesanguine.services

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.gabryel.reginaesanguine.domain.PlayerPosition

fun interface ResourceLoader {
    @Composable
    fun loadCardImage(pack: String, player: PlayerPosition, id: String): Painter?
}
