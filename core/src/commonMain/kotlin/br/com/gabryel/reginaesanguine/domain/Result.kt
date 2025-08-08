package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.util.ResultRaise
import br.com.gabryel.reginaesanguine.domain.util.buildResult

sealed interface Result<out S> {
    fun <T> map(transform: ResultRaise<T>.(S) -> T): Result<T>

    fun <T> flatmap(transform: (S) -> Result<T>): Result<T>

    fun orNull(): S?
}

data class Success<S>(val value: S) : Result<S> {
    override fun <T> map(transform: ResultRaise<T>.(S) -> T) = buildResult { transform(value) }

    override fun <T> flatmap(transform: (S) -> Result<T>) = transform(value)

    override fun orNull() = value
}

sealed interface Failure : Result<Nothing> {
    override fun <T> map(transform: ResultRaise<T>.(Nothing) -> T) = this

    override fun <T> flatmap(transform: (Nothing) -> Result<T>) = this

    override fun orNull() = null

    // Game State Errors
    object GameEnded : Failure

    data class NotPlayerTurn(val turn: Game) : Failure

    // Player State Errors
    data class CardNotOnHand(val card: String) : Failure

    // Board/Cell State Errors
    data class CellOutOfBoard(val position: Position) : Failure

    data class CellRankLowerThanCard(val cell: Cell) : Failure

    data class CellDoesNotBelongToPlayer(val cell: Cell) : Failure

    data class CellOccupied(val cell: Cell) : Failure

    class UnknownError(val error: Throwable) : Failure {
        override fun toString() = "Unknown Error: $error"
    }
}
