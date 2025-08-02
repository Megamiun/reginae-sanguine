package br.com.gabryel.reginaesanguine.app.services

import androidx.compose.ui.graphics.Color
import br.com.gabryel.reginaesanguine.app.ui.theme.Emerald
import br.com.gabryel.reginaesanguine.app.ui.theme.EmeraldAccent
import br.com.gabryel.reginaesanguine.app.ui.theme.Ruby
import br.com.gabryel.reginaesanguine.app.ui.theme.RubyAccent
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT

class PlayerContext(
    val position: PlayerPosition,
    val color: Color,
    val colorAccent: Color,
) {
    companion object {
        val left = PlayerContext(LEFT, Emerald, EmeraldAccent)
        val right = PlayerContext(RIGHT, Ruby, RubyAccent)

        fun getDefaultFor(position: PlayerPosition) = if (position == LEFT) left else right
    }
}
