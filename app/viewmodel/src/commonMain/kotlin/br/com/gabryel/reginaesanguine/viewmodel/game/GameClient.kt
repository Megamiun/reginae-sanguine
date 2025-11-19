package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto

/**
 * Service interface for game operations with UUID-based session management.
 * Uses DTOs for communication to support both local and remote implementations.
 */
interface GameClient {
    suspend fun submitAction(gameId: String, action: Action<out String>): GameViewDto

    suspend fun fetchStatus(gameId: String): GameViewDto

    suspend fun skip(gameId: String): GameViewDto =
        submitAction(gameId, Action.Skip)

    suspend fun play(
        gameId: String,
        position: Position,
        cardId: String
    ): GameViewDto =
        submitAction(gameId, Action.Play(position, cardId))
}
