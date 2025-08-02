package br.com.gabryel.reginaesanguine.viewmodel

import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.State.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.State.ChooseCard
import br.com.gabryel.reginaesanguine.viewmodel.State.ChoosePosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class GameViewModel(private var stateFlow: MutableStateFlow<State>) {
    val state = stateFlow.asStateFlow()

    companion object {
        fun forGame(game: Game) = GameViewModel(MutableStateFlow(ChooseAction(game)))
    }

    fun skip() = update(ChooseAction::skip)

    fun toChooseCard() = update(ChooseAction::toChooseCard)

    fun chooseCard(cardId: String) = update<ChooseCard> { state ->
        state.chooseCard(cardId)
    }

    fun choosePosition(position: Position) = update<ChoosePosition> { state ->
        state.play(position)
    }

    fun play(position: Position, cardId: String) = update<Playable> { state ->
        state.play(position, cardId)
    }

    private inline fun <reified T> update(execute: (T) -> State): Boolean {
        val previousState = state.value
        require<T>(previousState)

        stateFlow.value = execute(previousState)
        return stateFlow.value.error == null
    }
}

@OptIn(ExperimentalContracts::class)
private inline fun <reified T> require(item: Any) {
    contract {
        returns() implies (item is T)
    }

    require(item is T) { "${item::class.simpleName} is not ${T::class.simpleName}" }
}
