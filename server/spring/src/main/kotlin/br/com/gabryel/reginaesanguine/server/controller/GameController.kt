package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController {
    private val packs = mutableMapOf<String, Pack>()

    private val games = mutableMapOf<UUID, Game>()
    private val gamePackIds = mutableMapOf<UUID, String>()

    @PostMapping
    fun initGame(
        @RequestBody request: InitGameRequest
    ): GameIdDto {
        val gameId = UUID.randomUUID()
        val pack = packs[request.packId]
            ?: throw ResponseStatusException(NOT_FOUND, "Pack ${request.packId} not found")

        // Create entities later for storing data and improve this validation
        val availableCards = pack.cards.associateBy { it.id }
        val deck = request.deckCardIds.mapNotNull { availableCards[it] }

        if (deck.size != request.deckCardIds.size) {
            throw ResponseStatusException(BAD_REQUEST, "Invalid card IDs in deck")
        }

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
        return GameIdDto(gameId)
    }

    @PostMapping("/{gameId}/action")
    fun executeAction(
        @PathVariable gameId: UUID,
        @RequestHeader("Authorization") authorization: String,
        @RequestBody action: Action<String>
    ): GameViewDto {
        val playerPosition = PlayerPosition.valueOf(authorization)
        val game = getGameBy(gameId)

        return when (val result = game.play(game.playerTurn, action)) {
            is Success -> {
                games[gameId] = result.value
                GameViewDto.from(GameView.forPlayer(result.value, playerPosition), getPackId(gameId))
            }
            is Failure -> throw ResponseStatusException(BAD_REQUEST, result.toString())
        }
    }

    @GetMapping("/{gameId}/status")
    fun fetchStatus(
        @PathVariable gameId: UUID,
        @RequestHeader("Authorization") authorization: String
    ): GameViewDto? {
        val playerPosition = PlayerPosition.valueOf(authorization)
        return GameViewDto.from(GameView.forPlayer(getGameBy(gameId), playerPosition), getPackId(gameId))
    }

    private fun getPackId(gameId: UUID): String =
        gamePackIds[gameId] ?: throw ResponseStatusException(NOT_FOUND, "Pack ID for game $gameId not found")

    private fun getGameBy(gameId: UUID): Game =
        games[gameId] ?: throw ResponseStatusException(NOT_FOUND, "Game $gameId not found")
}
