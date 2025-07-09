package br.com.gabryel.reginarsanguine.domain.matchers

import br.com.gabryel.reginarsanguine.domain.Cell
import br.com.gabryel.reginarsanguine.domain.CellContainer
import br.com.gabryel.reginarsanguine.domain.Position
import br.com.gabryel.reginarsanguine.domain.column
import br.com.gabryel.reginarsanguine.domain.row
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.all

fun haveCell(
    position: Position,
    match: Matcher<Cell>,
) = Matcher<CellContainer> { board ->
    val cell = board shouldHaveCellAt position

    val result = match.test(cell)
    MatcherResult(
        result.passed(),
        { "Cell at [${position.row()}, ${position.column()}]: " + result.failureMessage() },
        { "Cell at [${position.row()}, ${position.column()}]: " + result.negatedFailureMessage() },
    )
}

fun haveCells(vararg cellDescriptors: Pair<Position, Matcher<Cell>>) =
    Matcher<CellContainer> { board ->
        val cellMatchers =
            cellDescriptors.map { (position, match) ->
                haveCell(position, match)
            }

        Matcher.all(matchers = cellMatchers.toTypedArray()).test(board)
    }

infix fun CellContainer.shouldHaveCellAt(position: Position): Cell = getCellAt(position).shouldBeSuccess()
