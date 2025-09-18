package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Position

/**
 * Abstraction for game execution logic that handles the difference between
 * local gameplay (immediate execution) and remote gameplay (server communication/timeouts).
 */
interface GameManager {
    /**
     * Execute a skip action for the current player.
     * @return New GameState representing the result
     */
    suspend fun skip(): GameState

    /**
     * Execute a play action with the specified card at the given position.
     * @param position Board position to play the card
     * @param cardId ID of the card to play
     * @return New GameState representing the result
     */
    suspend fun play(position: Position, cardId: String): GameState

    /**
     * Check if a specific play action would be valid.
     * @param position Board position to check
     * @param cardId ID of the card to check
     * @return true if the play would be valid
     */
    fun isPlayable(position: Position, cardId: String): Boolean
}
