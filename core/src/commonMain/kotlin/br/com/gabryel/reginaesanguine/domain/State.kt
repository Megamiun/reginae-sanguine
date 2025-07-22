package br.com.gabryel.reginaesanguine.domain

sealed interface State {
    data object Ongoing : State

    sealed interface Ended : State {
        data object Tie : Ended

        data class Won(val player: PlayerPosition) : Ended
    }
}
