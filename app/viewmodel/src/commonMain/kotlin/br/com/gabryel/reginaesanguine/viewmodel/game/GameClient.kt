package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Action
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.Position
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest

/**
 * Service interface for game operations with UUID-based session management.
 * Uses DTOs for communication to support both local and remote implementations.
 */
interface GameClient {
    suspend fun initGame(request: InitGameRequest): GameIdDto

    suspend fun submitAction(
        gameId: String,
        playerPosition: PlayerPosition,
        action: Action<out String>
    ): GameViewDto

    suspend fun fetchStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto?

    suspend fun skip(gameId: String, playerPosition: PlayerPosition): GameViewDto =
        submitAction(gameId, playerPosition, Action.Skip)

    suspend fun play(
        gameId: String,
        playerPosition: PlayerPosition,
        position: Position,
        cardId: String
    ): GameViewDto =
        submitAction(gameId, playerPosition, Action.Play(position, cardId))
}
