package br.com.gabryel.reginaesanguine.server.domain

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.State
import kotlinx.serialization.Serializable

@Serializable
sealed interface StateDto {
    @Serializable
    data object Ongoing : StateDto

    @Serializable
    sealed interface Ended : StateDto {
        @Serializable
        data object Tie : Ended

        @Serializable
        data class Won(val player: PlayerPosition) : Ended
    }

    companion object {
        fun from(state: State): StateDto = when (state) {
            is State.Ongoing -> Ongoing
            is State.Ended.Tie -> Ended.Tie
            is State.Ended.Won -> Ended.Won(state.player)
        }
    }
}
