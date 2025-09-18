package br.com.gabryel.reginaesanguine.viewmodel.game

import br.com.gabryel.reginaesanguine.domain.Position

interface Playable {
    val manager: GameManager

    suspend fun skip() = manager.skip()

    suspend fun play(position: Position, cardId: String) = manager.play(position, cardId)

    fun isPlayable(position: Position, cardId: String): Boolean = manager.isPlayable(position, cardId)
}

sealed interface GameState {
    val error: String?
}

sealed interface ActiveGameState : GameState {
    val game: GamePlayerSummary
}

sealed interface Awaitable : GameState {
    suspend fun trigger(callback: (GameState) -> Unit)
}

data class ChooseAction(
    override val manager: GameManager,
    override val game: GamePlayerSummary,
    override val error: String? = null
) : ActiveGameState, Playable {
    fun toChooseCard() = ChooseCard(manager, game)
}

data class ChooseCard(
    override val manager: GameManager,
    override val game: GamePlayerSummary,
    override val error: String? = null
) : ActiveGameState, Playable {
    fun chooseCard(cardId: String) = ChoosePosition(manager, game, cardId)
}

data class ChoosePosition(
    override val manager: GameManager,
    override val game: GamePlayerSummary,
    val cardId: String,
    override val error: String? = null
) : ActiveGameState, Playable {
    suspend fun play(position: Position) = play(position, cardId)
}

data class AwaitTurn(
    override val game: GamePlayerSummary,
    override val error: String? = null,
    private val execute: suspend () -> GameState
) : ActiveGameState, Awaitable {
    override suspend fun trigger(callback: (GameState) -> Unit) {
        callback(execute())
    }
}

data class AwaitMatch(
    override val error: String? = null,
    private val execute: suspend () -> GameState
) : GameState, Awaitable {
    override suspend fun trigger(callback: (GameState) -> Unit) {
        callback(execute())
    }
}
