package br.com.gabryel.reginarsanguine.domain

data class Board(
    private val width: Int,
    private val height: Int,
    private val state: Map<Pair<Int, Int>, Cell> = mapOf()
): BoardLike, Playable<Board> {

    companion object {
        fun default() = Board(5, 3)
    }

    override fun play(player: PlayerPosition, action: Action): Board {
        return when (action) {
            is Action.Skip -> this
            is Action.Play -> {
                val cell = at(action.row, action.column).copy(
                    owner = player,
                    card = action.card
                )

                copy(state = state + (action.row to action.column to cell))
            }
        }
    }

    override fun at(row: Int, column: Int): Cell {
        return state[row to column] ?: Cell.EMPTY
    }
}