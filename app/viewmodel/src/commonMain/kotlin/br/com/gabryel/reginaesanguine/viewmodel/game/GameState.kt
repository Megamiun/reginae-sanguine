package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.domain.Size
import br.com.gabryel.reginaesanguine.domain.State

/**
 * Interface providing UI-safe access to game information.
 * This ensures the UI gets the information it needs without breaking the GameClient abstraction.
 */
interface GameStateData {
    val size: Size
    val playerTurn: PlayerPosition
    val localPlayerPosition: PlayerPosition
    val playerHandPosition: PlayerPosition
    val currentPlayerHand: List<Card>
    val round: Int

    fun getState(): State

    fun getScores(): Map<PlayerPosition, Int>

    fun getWinner(): PlayerPosition?

    fun getCellAt(position: Position): Cell?

    fun getBaseLaneScoreAt(lane: Int): Map<PlayerPosition, Int>

    fun getLaneWinner(lane: Int): PlayerPosition?
}

interface Playable {
    suspend fun play(position: Position, cardId: String): GameState
}

sealed interface GameState {
    val client: GameManager
    val error: String?
    val game: GameStateData

    data class ChooseAction(
        override val client: GameManager,
        override val game: GameStateData,
        override val error: String? = null
    ) : GameState, Playable {
        fun toChooseCard() = ChooseCard(client, game)

        override suspend fun play(position: Position, cardId: String) = client.play(position, cardId)

        suspend fun skip() = client.skip()
    }

    data class ChooseCard(
        override val client: GameManager,
        override val game: GameStateData,
        override val error: String? = null
    ) : GameState, Playable {
        fun chooseCard(cardId: String) = ChoosePosition(client, game, cardId)

        override suspend fun play(position: Position, cardId: String) = client.play(position, cardId)
    }

    data class ChoosePosition(
        override val client: GameManager,
        override val game: GameStateData,
        val cardId: String,
        override val error: String? = null
    ) : GameState {
        suspend fun play(position: Position) = client.play(position, cardId)
    }

    data class Wait(
        override val client: GameManager,
        override val game: GameStateData,
        override val error: String? = null,
        private val execute: suspend () -> GameState
    ) : GameState {
        suspend fun trigger(callback: (GameState) -> Unit) {
            callback(execute())
        }
    }
}
