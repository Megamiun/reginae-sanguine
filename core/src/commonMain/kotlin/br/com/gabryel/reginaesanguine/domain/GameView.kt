package br.com.gabryel.reginaesanguine.domain

/**
 * Minimal view for remote clients - contains only essential information.
 * No opponent information is exposed.
 */
data class GameView(
    val boardScores: Map<PlayerPosition, Int>,
    val localPlayerHand: List<Card>,
    val localPlayerDeckSize: Int,
    val localPlayerPosition: PlayerPosition,
    val playerTurn: PlayerPosition,
    val round: Int = 1,
    val state: State,
    val boardCells: Map<Position, Cell> = emptyMap(),
    val laneScores: Map<Int, Map<PlayerPosition, Int>> = emptyMap(),
    val laneWinners: Map<Int, PlayerPosition?> = emptyMap()
) {
    fun getScores(): Map<PlayerPosition, Int> = boardScores

    fun getWinner(): PlayerPosition? = getScores().getWinner()

    private fun Map<PlayerPosition, Int>.getWinner(): PlayerPosition? {
        val max = maxBy { it.value }
        if (values.all { it == max.value }) return null
        return max.key
    }

    companion object {
        fun forPlayer(game: Game, playerPosition: PlayerPosition): GameView {
            val player = game.players[playerPosition]
                ?: throw IllegalStateException("Player not found")

            val boardCells = (0 until game.size.width).flatMap { x ->
                (0 until game.size.height).mapNotNull { y ->
                    val position = Position(x, y)
                    when (val result = game.getCellAt(position)) {
                        is Success<Cell> -> position to result.value
                        is Failure -> null
                    }
                }
            }.toMap()

            return GameView(
                boardScores = game.getScores(),
                localPlayerHand = player.hand,
                localPlayerDeckSize = player.deck.size,
                localPlayerPosition = playerPosition,
                playerTurn = game.playerTurn,
                round = game.round,
                boardCells = boardCells,
                laneScores = (0 until game.size.height).associateWith { lane -> game.getBaseLaneScoreAt(lane) },
                laneWinners = (0 until game.size.height).associateWith { lane -> game.getLaneWinner(lane) },
                state = game.getState(),
            )
        }
    }
}
