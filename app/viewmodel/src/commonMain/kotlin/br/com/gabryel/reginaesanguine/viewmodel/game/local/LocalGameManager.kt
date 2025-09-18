package br.com.gabryel.reginaesanguine.viewmodel.game.local

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.viewmodel.game.ChooseAction
import br.com.gabryel.reginaesanguine.viewmodel.game.GameManager
import br.com.gabryel.reginaesanguine.viewmodel.game.GameState

/**
 * Local game client that executes game actions immediately using the full game state.
 * This maintains the original local gameplay behavior where both players' information
 * is available and actions are executed synchronously.
 */
class LocalGameManager(private val currentGame: Game) : GameManager {
    override suspend fun skip() = update(Action.Skip)

    override suspend fun play(position: Position, cardId: String) = update(Play(position, cardId))

    private fun update(action: Action<out String>): GameState =
        when (val result = currentGame.play(currentGame.playerTurn, action)) {
            is Success<Game> -> ChooseAction(LocalGameManager(result.value), LocalGameStateData(result.value))
            is Failure -> ChooseAction(this, LocalGameStateData(currentGame), error = result.toString())
        }

    override fun isPlayable(position: Position, cardId: String): Boolean =
        currentGame.play(currentGame.playerTurn, Play(position, cardId)) is Success
}
