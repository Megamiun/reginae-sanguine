package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.repository.AccountDeckRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GameService(
    private val deckService: DeckService,
    private val accountDeckRepository: AccountDeckRepository,
) {
    private val games = mutableMapOf<String, Game>()
    private val gamePackIds = mutableMapOf<String, String>()
    private val gamePositions = mutableMapOf<String, Map<String, PlayerPosition>>()
    private val gameDeckStateIds = mutableMapOf<String, Map<String, String>>()

    suspend fun initGame(request: InitGameRequest): String {
        val gameId = Uuid.random().toString()

        val packAlias = "queens_blood"
        val pack = requireNotNull(deckService.loadPack(packAlias)) { "Pack with alias $packAlias not found" }
        val availableCards = pack.cards.associateBy { it.id }

        val creatorDeck = getPlayerDeck(availableCards, request.creatorDeckStateId)
        val joinerDeck = getPlayerDeck(availableCards, request.joinerDeckStateId)

        val player = Player(creatorDeck.take(5), creatorDeck.drop(5))
        val opponent = Player(joinerDeck.take(5), joinerDeck.drop(5))

        val game = Game(
            Board.default(),
            mapOf(request.creatorPosition to player, request.creatorPosition.opponent to opponent),
            playerTurn = LEFT,
            availableCards = availableCards,
        )

        games[gameId] = game
        gamePackIds[gameId] = pack.id
        gameDeckStateIds[gameId] = mapOf(
            request.creatorId to request.creatorDeckStateId,
            request.joinerId to request.joinerDeckStateId
        )
        gamePositions[gameId] = mapOf(
            request.creatorId to request.creatorPosition,
            request.joinerId to request.creatorPosition.opponent
        )
        return gameId
    }

    fun executeAction(gameId: String, accountId: String, action: Action<out String>): GameViewDto {
        val game = getGameBy(gameId)
        val packId = requireNotNull(gamePackIds[gameId]) { "Pack ID for game $gameId not found" }
        val deckStateId = requireNotNull(gameDeckStateIds[gameId]?.get(accountId)) {
            "Deck state ID for game $gameId not found"
        }
        val playerPosition = requireNotNull(gamePositions[gameId]?.get(accountId)) {
            "Player Position game $gameId not found"
        }

        return when (val result = game.play(game.playerTurn, action)) {
            is Success -> {
                games[gameId] = result.value
                val gameView = GameView.forPlayer(result.value, playerPosition)
                GameViewDto.from(gameId, gameView, packId, deckStateId)
            }

            is Failure -> throw IllegalArgumentException(result.toString())
        }
    }

    fun fetchStatus(gameId: String, accountId: String): GameViewDto? {
        val game = games[gameId] ?: return null
        val packId = gamePackIds[gameId] ?: return null
        val deckStateId = gameDeckStateIds[gameId]?.get(accountId) ?: return null
        val playerPosition = gamePositions[gameId]?.get(accountId) ?: return null

        return GameViewDto.from(gameId, GameView.forPlayer(game, playerPosition), packId, deckStateId)
    }

    private fun getGameBy(gameId: String): Game =
        games[gameId] ?: throw IllegalArgumentException("Game $gameId not found")

    private suspend fun getPlayerDeck(availableCards: Map<String, Card>, deckStateId: String): List<Card> {
        val accountDeck = requireNotNull(accountDeckRepository.findByStateId(deckStateId)) {
            "Deck state $deckStateId not found"
        }

        val deck = accountDeck.cardIds.mapNotNull { availableCards[it] }
        require(deck.size == accountDeck.cardIds.size) { "Invalid card IDs in deck" }
        return deck.shuffled()
    }
}
