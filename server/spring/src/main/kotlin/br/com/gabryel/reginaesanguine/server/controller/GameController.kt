package br.com.gabryel.reginaesanguine.server.controller

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Game
import br.com.gabryel.reginaesanguine.domain.Player
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.server.domain.GameState
import br.com.gabryel.reginaesanguine.server.domain.GameState.ONGOING
import br.com.gabryel.reginaesanguine.server.domain.GameSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("game")
class GameController {
    val games = mutableMapOf<UUID, Game>()

    @PostMapping
    fun createGame(
        @RequestHeader("Authorization") authorization: String,
    ): GameSummary {
        val id = UUID.randomUUID()
        val player = Player(listOf(), listOf())
        val game = Game.forPlayers(player, player)

        games[id] = game

        return GameSummary(id, ONGOING, LEFT)
    }

    @GetMapping("/{id}")
    fun getGame(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: UUID
    ): GameSummary {
        val game = getGameBy(id)

        return GameSummary(id, ONGOING, game.playerTurn)
    }

    @PostMapping("/{id}/action")
    fun play(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: UUID,
        @RequestBody action: Action<String>
    ): GameSummary {
        val player = PlayerPosition.valueOf(authorization)
        when (val result = getGameBy(id).play(player, action)) {
            is Success<Game> -> {
                val game = result.value
                games[id] = game
                return GameSummary(id, GameState.from(game.getState()), result.value.playerTurn)
            }
            is Failure -> throw IllegalStateException(result.toString())
        }
    }

    private fun getGameBy(id: UUID): Game = games[id] ?: error("Game with id $id not found")
}
