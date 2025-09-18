package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState.ChoosePosition
import br.com.gabryel.reginaesanguine.viewmodel.require
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val stateFlow: MutableStateFlow<GameState>,
    private val coroutineScope: CoroutineScope
) {
    val state = stateFlow.asStateFlow()

    companion object {
        fun forLocalGame(game: Game, coroutineScope: CoroutineScope): GameViewModel {
            val client = LocalGameManager(game)
            val gameUIData = LocalGameStateData(game)
            return GameViewModel(MutableStateFlow(ChooseAction(client, gameUIData)), coroutineScope)
        }

        fun forRemoteGame(game: Game, coroutineScope: CoroutineScope, localPlayerPosition: PlayerPosition): GameViewModel {
            val client = RemoteGameManager.create(game, localPlayerPosition)
            val gameView = GameView.forPlayer(game, localPlayerPosition)
            val gameUIData = RemoteGameUIData(gameView)
            return GameViewModel(MutableStateFlow(ChooseAction(client, gameUIData)), coroutineScope)
        }
    }

    fun skip(): Boolean = update(ChooseAction::skip)

    fun play(position: Position, cardId: String): Boolean =
        update<Playable> { state -> state.play(position, cardId) }

    fun isPlayable(position: Position, cardId: String): Boolean =
        state.value.client.isPlayable(position, cardId)

    fun toChooseCard(): Boolean = update(ChooseAction::toChooseCard)

    fun chooseCard(cardId: String): Boolean =
        update<GameState.ChooseCard> { state -> state.chooseCard(cardId) }

    fun choosePosition(position: Position): Boolean =
        update<ChoosePosition> { state -> state.play(position) }

    private inline fun <reified T> update(crossinline execute: suspend (T) -> GameState): Boolean {
        val previousState = state.value
        require<T>(previousState)

        coroutineScope.launch {
            val newState = execute(previousState)
            stateFlow.value = newState

            if (newState is GameState.Wait) {
                newState.trigger { stateFlow.value = it }
            }
        }
        return true
    }
}
