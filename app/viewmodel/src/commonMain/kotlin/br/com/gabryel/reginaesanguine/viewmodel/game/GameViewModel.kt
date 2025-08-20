package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.require
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(private val stateFlow: MutableStateFlow<GameState>) {
    val state = stateFlow.asStateFlow()

    companion object {
        fun forGame(game: Game) = GameViewModel(MutableStateFlow(GameState.ChooseAction(game)))
    }

    fun skip() = update(GameState.ChooseAction::skip)

    fun toChooseCard() = update(GameState.ChooseAction::toChooseCard)

    fun chooseCard(cardId: String) = update<GameState.ChooseCard> { state ->
        state.chooseCard(cardId)
    }

    fun choosePosition(position: Position) = update<GameState.ChoosePosition> { state ->
        state.play(position)
    }

    fun play(position: Position, cardId: String) = update<Playable> { state ->
        state.play(position, cardId)
    }

    private inline fun <reified T> update(execute: (T) -> GameState): Boolean {
        val previousState = state.value
        require<T>(previousState)

        stateFlow.value = execute(previousState)
        return stateFlow.value.error == null
    }
}
