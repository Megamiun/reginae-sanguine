package br.com.gabryel.reginarsanguine.domain

sealed interface Result<out S> {
    fun <T> map(transform: (S) -> T): Result<T>

    fun <T> flatmap(transform: (S) -> Result<T>): Result<T>
}

data class Success<S>(val value: S) : Result<S> {
    override fun <T> map(transform: (S) -> T) = Success(transform(value))

    override fun <T> flatmap(transform: (S) -> Result<T>) = transform(value)
}

sealed interface Failure : Result<Nothing> {
    override fun <T> map(transform: (Nothing) -> T) = this

    override fun <T> flatmap(transform: (Nothing) -> Result<T>) = this

    data class NotPlayerTurn(val turn: Game) : Failure

    data class OutOfBoard(val position: Position) : Failure

    data class NotEnoughPins(val cell: Cell) : Failure

    data class CellDoesNotBelongToPlayer(val cell: Cell) : Failure

    data class CellOccupied(val cell: Cell) : Failure
}
