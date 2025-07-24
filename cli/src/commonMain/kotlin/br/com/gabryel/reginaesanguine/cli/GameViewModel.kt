package br.com.gabryel.reginaesanguine.cli

import br.com.gabryel.reginaesanguine.cli.State.ChooseAction
import br.com.gabryel.reginaesanguine.cli.State.ChooseCard
import br.com.gabryel.reginaesanguine.cli.State.ChoosePosition
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class GameViewModel(private var stateFlow: MutableStateFlow<State>) {
    val state = stateFlow.asStateFlow()

    companion object {
        fun forGame(game: Game) = GameViewModel(MutableStateFlow(ChooseAction(game)))
    }

    fun toChooseCard() {
        val currentState = state.value
        require<ChooseAction>(currentState)

        stateFlow.value = currentState.toChooseCard()
    }

    fun chooseCard(card: Card) {
        val currentState = state.value
        require<ChooseCard>(currentState)

        stateFlow.value = currentState.chooseCard(card)
    }

    fun choosePosition(position: Position) {
        val currentState = state.value
        require<ChoosePosition>(currentState)

        stateFlow.value = currentState.choosePosition(position)
    }

    fun skip() {
        val currentState = state.value
        require<ChooseAction>(currentState)

        stateFlow.value = currentState.skip()
    }
}

sealed interface State {
    val game: Game
    val error: String?

    data class ChooseAction(override val game: Game, override val error: String? = null) : State {
        fun toChooseCard() = ChooseCard(game)

        fun skip() = when (val game = game.play(game.nextPlayer, Skip)) {
            is Success<Game> -> ChooseAction(game.value)
            is Failure -> copy(error = game.toString())
        }
    }

    data class ChooseCard(override val game: Game, override val error: String? = null) : State {
        fun chooseCard(card: Card) = ChoosePosition(game, card)
    }

    data class ChoosePosition(override val game: Game, val card: Card, override val error: String? = null) : State {
        fun choosePosition(position: Position) = when (val game = game.play(game.nextPlayer, Play(position, card.id))) {
            is Success<Game> -> ChooseAction(game.value)
            is Failure -> copy(error = game.toString())
        }
    }
}

@OptIn(ExperimentalContracts::class)
private inline fun <reified T> require(item: Any) {
    contract {
        returns() implies (item is T)
    }

    require(item is T) { "${item::class.simpleName} is not ${T::class.simpleName}" }
}
