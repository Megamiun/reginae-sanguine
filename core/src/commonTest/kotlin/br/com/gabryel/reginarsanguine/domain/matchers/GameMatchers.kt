package br.com.gabryel.reginarsanguine.domain.matchers

import br.com.gabryel.reginarsanguine.domain.Game
import br.com.gabryel.reginarsanguine.domain.PlayerPosition
import org.junit.jupiter.api.fail

fun Game.havePlayerOn(position: PlayerPosition) =
    players[position] ?: fail { "No player on position $position, this should not be possible" }
