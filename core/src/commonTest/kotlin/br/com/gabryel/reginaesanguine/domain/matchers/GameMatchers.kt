package br.com.gabryel.reginaesanguine.domain.matchers

import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import org.junit.jupiter.api.fail

fun Game.havePlayerOn(position: PlayerPosition) =
    players[position] ?: fail { "No player on position $position, this should not be possible" }
