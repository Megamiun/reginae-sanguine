package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.PlayableMove
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import kotlinx.coroutines.delay
import kotlin.random.Random.Default.nextLong

/**
 * Local implementation of GameClient for testing.
 * In real implementation, this would be replaced with server communication.
 */
class LocalGameClient(val delayInMillis: Long) : GameClient {
    private val games = mutableMapOf<String, Game>()

    override suspend fun initGame(deck: List<Card>, position: PlayerPosition, pack: Pack): String {
        val gameId = "local-${nextLong()}"
        val player = Player(deck.take(5), deck.drop(5))

        games[gameId] = Game(
            Board.default(),
            mapOf(position to player, position.opponent to Player()),
            playerTurn = LEFT,
            availableCards = pack.cards.associateBy { it.id },
        )
        return gameId
    }

    override suspend fun skip(gameId: String, playerPosition: PlayerPosition): GameView {
        val game = games[gameId] ?: error("Game with $gameId not available")

        return when (val result = game.play(game.playerTurn, Skip)) {
            is Success -> {
                games[gameId] = result.value
                GameView.forPlayer(result.value, playerPosition)
            }
            is Failure -> GameView.forPlayer(game, playerPosition)
        }
    }

    override suspend fun play(gameId: String, playerPosition: PlayerPosition, position: Position, cardId: String): GameView {
        val game = games[gameId] ?: error("Game with $gameId not available")

        return when (val result = game.play(game.playerTurn, Play(position, cardId))) {
            is Success -> {
                games[gameId] = result.value
                GameView.forPlayer(result.value, playerPosition)
            }
            is Failure -> GameView.forPlayer(game, playerPosition)
        }
    }

    override suspend fun fetchStatus(gameId: String, playerPosition: PlayerPosition): GameView? {
        val game = games[gameId] ?: return null
        delay(delayInMillis)

        if (game.playerTurn == playerPosition || game.getState() != State.Ongoing)
            return GameView.forPlayer(game, playerPosition)

        skip(gameId, game.playerTurn)
        return fetchStatus(gameId, playerPosition)
    }
}
