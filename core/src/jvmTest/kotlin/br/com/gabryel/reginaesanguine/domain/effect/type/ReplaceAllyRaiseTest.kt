package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Board
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.DOWNWARD
import br.com.gabryel.reginaesanguine.domain.Failure.CellWithNoCardToReplace
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.B1
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.matchers.cardCellWith
import br.com.gabryel.reginaesanguine.domain.matchers.haveCell
import br.com.gabryel.reginaesanguine.domain.matchers.haveCellTotalPower
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeFailure
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccessfulAnd
import br.com.gabryel.reginaesanguine.domain.util.buildResult
import kotlin.test.Test

class ReplaceAllyRaiseTest {
    @Test
    fun `when playing a card with a Replace Effect over a card, should replace current card`() {
        val originalAlly = cardOf(power = 2)

        val replaceCard = cardOf(power = 1, effect = ReplaceAllyRaise(1, ANY))

        val game = buildResult {
            Board.default()
                .play(LEFT, Play(A1, originalAlly)).orRaiseError().board
                .play(LEFT, Play(A1, replaceCard)).orRaiseError().board
        }

        game shouldBeSuccessfulAnd haveCell(A1, cardCellWith(LEFT, replaceCard))
    }

    @Test
    fun `when playing a card with a Replace Effect over a card, should apply new effect`() {
        val originalAlly = cardOf(power = 2)

        val replaceEffect = ReplaceAllyRaise(1, ANY, affected = setOf(DOWNWARD))
        val replaceCard = cardOf(power = 1, effect = replaceEffect)

        val game = buildResult {
            Board.default()
                .play(LEFT, Play(A1, originalAlly)).orRaiseError().board
                .play(LEFT, Play(B1, originalAlly)).orRaiseError().board
                .play(LEFT, Play(A1, replaceCard)).orRaiseError().board
        }

        game shouldBeSuccessfulAnd haveCellTotalPower(B1, 4)
    }

    @Test
    fun `when playing a card with a Replace Effect over an empty cell, should fail with CellWithNoCardToReplace`() {
        val replaceCard = cardOf(effect = ReplaceAllyRaise(1, ANY))

        val result = Board.default()
            .play(LEFT, Play(A1, replaceCard))

        result.shouldBeFailure<CellWithNoCardToReplace>()
    }
}
