package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.local.LocalGameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.local.LocalGameStateData
import br.com.gabryel.reginaesanguine.viewmodel.game.remote.RemoteGameManager
import br.com.gabryel.reginaesanguine.viewmodel.require
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val stateFlow: MutableStateFlow<GameState>, private val coroutineScope: CoroutineScope) {
    val state = stateFlow.asStateFlow()

    companion object {
        fun forLocalGame(game: Game, coroutineScope: CoroutineScope): GameViewModel {
            val client = LocalGameManager(game)
            val state = LocalGameStateData(game)
            return GameViewModel(MutableStateFlow(ChooseAction(client, state)), coroutineScope)
        }

        suspend fun forRemoteGame(
            pack: Pack,
            deck: List<Card>,
            position: PlayerPosition,
            client: GameClient,
            coroutineScope: CoroutineScope
        ): GameViewModel {
            val manager = RemoteGameManager.create(client, position, deck, pack)

            return GameViewModel(MutableStateFlow(manager.awaitGameCreation()), coroutineScope)
                .trigger()
        }
    }

    fun skip(): Boolean = update(ChooseAction::skip)

    fun play(position: Position, cardId: String): Boolean =
        update<Playable> { state -> state.play(position, cardId) }

    fun isPlayable(position: Position, cardId: String): Boolean =
        state.value.let { state -> state is Playable && state.isPlayable(position, cardId) }

    fun toChooseCard(): Boolean =
        update(ChooseAction::toChooseCard)

    fun chooseCard(cardId: String): Boolean =
        update<ChooseCard> { state -> state.chooseCard(cardId) }

    fun choosePosition(position: Position): Boolean =
        update<ChoosePosition> { state -> state.play(position) }

    private inline fun <reified T> update(crossinline execute: suspend (T) -> GameState): Boolean {
        val previousState = state.value
        require<T>(previousState)

        coroutineScope.launch {
            val newState = execute(previousState)
            stateFlow.value = newState

            trigger()
        }
        return true
    }

    private suspend fun trigger(): GameViewModel {
        val state = state.value
        if (state is Awaitable)
            state.trigger { stateFlow.value = it }

        return this
    }
}
