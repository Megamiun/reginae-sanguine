package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT

data class Turn(
    private val player: PlayerPosition,
    private val board: Board
) : BoardLike by board, TurnLike, Playable<Turn> {
    override val nextPlayer = when (player) {
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    companion object {
        fun default() = Turn(LEFT, Board.default())
    }

    override fun play(player: PlayerPosition, action: Action) =
        copy(
            player = player,
            board = board.play(player, action)
        )
}