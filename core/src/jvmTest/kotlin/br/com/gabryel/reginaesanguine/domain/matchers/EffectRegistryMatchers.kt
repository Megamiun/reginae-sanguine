package br.com.gabryel.reginaesanguine.domain.matchers

import br.com.gabryel.reginaesanguine.domain.CellContainer
import br.com.gabryel.reginaesanguine.domain.EffectRegistry
import br.com.gabryel.reginaesanguine.domain.Position
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.equals.beEqual

fun havePositionExtraPower(position: Position, power: Int, board: CellContainer) = Matcher<EffectRegistry> { registry ->
    val cellPower = registry.getExtraPowerAt(position, board)

    val result = beEqual(power).test(cellPower)
    MatcherResult(
        result.passed(),
        { "Extra Power at [${position.lane}, ${position.column}]: " + result.failureMessage() },
        { "Extra Power at [${position.lane}, ${position.column}]: " + result.negatedFailureMessage() },
    )
}
