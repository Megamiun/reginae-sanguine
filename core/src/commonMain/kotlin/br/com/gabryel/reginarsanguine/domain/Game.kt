package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT

data class Game(private val turns: List<Turn>): BoardLike, TurnLike, Playable<Game> {
    override val nextPlayer = getTurn().nextPlayer

    companion object {
        fun default() = Game(listOf())
    }

    override fun play(player: PlayerPosition, action: Action) =
        copy(turns = turns + getTurn().play(player, action))

    override fun at(row: Int, column: Int) = getTurn().at(row, column)

    private fun getTurn() = turns.lastOrNull()
        ?: Turn(RIGHT, Board.default())
}
