package br.com.gabryel.reginaesanguine.viewmodel.game.remote

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.State
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.viewmodel.game.GameClient
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.GameIdDto
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.GameViewDto
import br.com.gabryel.reginaesanguine.viewmodel.game.dto.InitGameRequest
import kotlinx.coroutines.delay
import kotlin.random.Random.Default.nextLong

/**
 * Local implementation of GameClient for testing.
 * In real implementation, this would be replaced with server communication.
 */
class LocalGameClient(val delayInMillis: Long, private val packs: Map<String, Pack>) : GameClient {
    private val games = mutableMapOf<String, Game>()
    private val gamePackIds = mutableMapOf<String, String>()

    override suspend fun initGame(request: InitGameRequest): GameIdDto {
        val pack = packs[request.packId] ?: error("Pack ${request.packId} not found")
        val availableCards = pack.cards.associateBy { it.id }
        val deck = request.deckCards.mapNotNull { availableCards[it] }

        if (deck.size != request.deckCards.size)
            error("Invalid card IDs in deck")

        val gameId = "local-${nextLong()}"
        val player = Player(deck.take(5), deck.drop(5))

        games[gameId] = Game(
            Board.default(),
            mapOf(request.position to player, request.position.opponent to Player()),
            playerTurn = LEFT,
            availableCards = availableCards,
        )
        gamePackIds[gameId] = request.packId
        return GameIdDto(gameId)
    }

    override suspend fun submitAction(gameId: String, playerPosition: PlayerPosition, action: Action<out String>): GameViewDto {
        val game = games[gameId] ?: error("Game with $gameId not available")
        val packId = gamePackIds[gameId] ?: error("Pack ID for game $gameId not found")

        return when (val result = game.play(game.playerTurn, action)) {
            is Success -> {
                games[gameId] = result.value
                GameViewDto.from(GameView.forPlayer(result.value, playerPosition), packId)
            }
            is Failure -> GameViewDto.from(GameView.forPlayer(game, playerPosition), packId)
        }
    }

    override suspend fun fetchStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto? {
        val game = games[gameId] ?: return null
        val packId = gamePackIds[gameId] ?: return null
        delay(delayInMillis)

        if (game.playerTurn == playerPosition || game.getState() != State.Ongoing)
            return GameViewDto.from(GameView.forPlayer(game, playerPosition), packId)

        skip(gameId, game.playerTurn)
        return fetchStatus(gameId, playerPosition)
    }
}
