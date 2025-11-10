package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GameService(private val deckService: DeckService) {
    private val games = mutableMapOf<String, Game>()
    private val gamePackIds = mutableMapOf<String, String>()

    suspend fun initGame(request: InitGameRequest): String {
        val gameId = Uuid.random().toString()
        val pack = deckService.loadPack(request.packId)
            ?: throw IllegalArgumentException("Pack ${request.packId} not found")

        val availableCards = pack.cards.associateBy { it.id }
        val deck = request.deckCardIds.mapNotNull { availableCards[it] }

        require(deck.size == request.deckCardIds.size) { "Invalid card IDs in deck" }

        val player = Player(deck.take(5), deck.drop(5))
        val opponent = Player(emptyList(), emptyList())

        val game = Game(
            Board.default(),
            mapOf(request.position to player, request.position.opponent to opponent),
            playerTurn = PlayerPosition.LEFT,
            availableCards = availableCards,
        )

        games[gameId] = game
        gamePackIds[gameId] = request.packId
        return gameId
    }

    fun executeAction(gameId: String, playerPosition: PlayerPosition, action: Action<out String>): GameViewDto {
        val game = getGameBy(gameId)
        val packId = gamePackIds[gameId]
            ?: throw IllegalArgumentException("Pack ID for game $gameId not found")

        return when (val result = game.play(game.playerTurn, action)) {
            is Success -> {
                games[gameId] = result.value
                GameViewDto.from(GameView.forPlayer(result.value, playerPosition), packId)
            }
            is Failure -> throw IllegalArgumentException(result.toString())
        }
    }

    fun fetchStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto? {
        val game = games[gameId] ?: return null
        val packId = gamePackIds[gameId] ?: return null
        return GameViewDto.from(GameView.forPlayer(game, playerPosition), packId)
    }

    private fun getGameBy(gameId: String): Game =
        games[gameId] ?: throw IllegalArgumentException("Game $gameId not found")
}
