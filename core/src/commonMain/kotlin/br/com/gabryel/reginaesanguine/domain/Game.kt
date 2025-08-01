package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Failure.GameEnded
import br.com.gabryel.reginaesanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.State.Ended
import br.com.gabryel.reginaesanguine.domain.State.Ended.Tie
import br.com.gabryel.reginaesanguine.domain.State.Ongoing
import br.com.gabryel.reginaesanguine.domain.util.buildResult

data class Game(
    private val board: Board,
    val players: Map<PlayerPosition, Player>,
    val action: Action<out String>? = null,
    val previous: Game? = null,
    val playerTurn: PlayerPosition = LEFT
) : CellContainer by board {
    val round: Int = 1 + (previous?.round ?: 0)

    val currentPlayer = players[playerTurn]
        ?: throw IllegalStateException("No player on $playerTurn on game")

    companion object {
        fun forPlayers(left: Player, right: Player, drawn: Int = 5) =
            Game(Board.default(), mapOf(LEFT to left.draw(drawn), RIGHT to right.draw(drawn)), playerTurn = LEFT)
    }

    fun play(player: PlayerPosition, action: Action<out String>): Result<Game> = buildResult {
        ensure(getState() !is Ended) { GameEnded }
        ensure(playerTurn == player) { NotPlayerTurn(this@Game) }

        val otherPlayerAfterDraw = players.getValue(player.opponent).draw()

        when (action) {
            is Skip -> copy(
                playerTurn = playerTurn.opponent,
                players = players + mapOf(player.opponent to otherPlayerAfterDraw),
                previous = copy(action = action),
            )
            is Play -> {
                val (playerAfterPlay, card) = currentPlayer
                    .selectCard(action.card)
                    .orRaiseError()

                val newPlayers = mapOf(player to playerAfterPlay, player.opponent to otherPlayerAfterDraw)

                val newBoard = board.play(player, Play(action.position, card)).orRaiseError()
                copy(playerTurn = playerTurn.opponent, players = newPlayers, board = newBoard, previous = copy(action = action))
            }
        }
    }

    fun getScores(): Map<PlayerPosition, Int> = board.getScores()

    fun getLaneScore(lane: Int): Map<PlayerPosition, Int> = board.getLaneScores(lane)

    fun getState(): State =
        if (previous?.action == Skip && previous.previous?.action == Skip)
            Tie
        else
            Ongoing

    fun getLaneWinner(lane: Int): PlayerPosition? = getLaneScore(lane).getWinner()

    fun getWinner(): PlayerPosition? = getScores().getWinner()

    private fun Map<PlayerPosition, Int>.getWinner(): PlayerPosition? {
        val max = maxBy { it.value }

        if (values.all { it == max.value })
            return null

        return max.key
    }
}
