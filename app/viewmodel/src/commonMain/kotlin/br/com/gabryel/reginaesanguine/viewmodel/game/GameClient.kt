package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.GameView
import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position

/**
 * Service interface for game operations with UUID-based session management.
 * In real implementation, this would communicate with a server.
 */
interface GameClient {
    suspend fun initGame(deck: List<Card>, position: PlayerPosition, pack: Pack): String

    suspend fun skip(gameId: String, playerPosition: PlayerPosition): GameView

    suspend fun play(gameId: String, playerPosition: PlayerPosition, position: Position, cardId: String): GameView

    suspend fun fetchStatus(gameId: String, playerPosition: PlayerPosition): GameView?

    fun isPlayable(gameId: String, playerPosition: PlayerPosition, position: Position, cardId: String): Boolean
}
