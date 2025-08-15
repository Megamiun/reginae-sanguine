package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ended.Won
import br.com.gabryel.reginaesanguine.domain.State.Ongoing
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GameSummary(
    @Contextual
    val id: UUID,
    val state: GameState,
    val currentPlayer: PlayerPosition?,
)

enum class GameState {
    LEFT_WON,
    RIGHT_WON,
    TIE,
    ONGOING;

    companion object {
        fun from(state: State): GameState = when (state) {
            Ongoing -> ONGOING
            Tie -> TIE
            is Won if state.player == LEFT -> LEFT_WON
            else -> RIGHT_WON
        }
    }
}
