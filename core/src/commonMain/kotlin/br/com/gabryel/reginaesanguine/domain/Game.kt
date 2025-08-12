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
    val playerTurn: PlayerPosition = LEFT,
    val availableCards: Map<String, Card> = emptyMap()
) : CellContainer by board {
    val round: Int = 1 + (previous?.round ?: 0)

    val currentPlayer = players[playerTurn]
        ?: throw IllegalStateException("No player on $playerTurn on game")

    companion object {
        fun forPlayers(left: Player, right: Player, drawn: Int = 5, availableCards: List<Card> = emptyList()) =
            Game(
                Board.default(),
                mapOf(LEFT to left.draw(drawn), RIGHT to right.draw(drawn)),
                playerTurn = LEFT,
                availableCards = availableCards.associateBy { it.id },
            )
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

                val result = board.play(player, Play(action.position, card)).orRaiseError()

                val modifiedPlayers = mapOf(
                    player to playerAfterPlay,
                    player.opponent to otherPlayerAfterDraw,
                ).mapValues { (position, player) ->
                    val modification = result.playerModifications[position] ?: return@mapValues player
                    player.addCardsToHand(modification.cardsToAdd.mapNotNull { availableCards[it] })
                }

                copy(
                    playerTurn = playerTurn.opponent,
                    players = modifiedPlayers,
                    board = result.board,
                    previous = copy(action = action),
                )
            }
        }
    }

    fun getScores(): Map<PlayerPosition, Int> = board.getScores()

    fun getState(): State =
        if (previous?.action == Skip && previous.previous?.action == Skip)
            Tie
        else
            Ongoing

    fun getLaneWinner(lane: Int): PlayerPosition? = board.getBaseLaneScoreAt(lane).getWinner()

    fun getWinner(): PlayerPosition? = getScores().getWinner()

    private fun Map<PlayerPosition, Int>.getWinner(): PlayerPosition? {
        val max = maxBy { it.value }

        if (values.all { it == max.value })
            return null

        return max.key
    }
}
