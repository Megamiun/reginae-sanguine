package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.DOWNWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.LEFTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.UPWARD
import br.com.gabryel.reginaesanguine.domain.Failure.CellWithNoCardToReplace
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ANY
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ENEMIES
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCardsDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRankDefault
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseWinnerLanesByLoserScore
import br.com.gabryel.reginaesanguine.domain.effect.type.ReplaceAllyRaise
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.A2
import br.com.gabryel.reginaesanguine.domain.helpers.A5
import br.com.gabryel.reginaesanguine.domain.helpers.B1
import br.com.gabryel.reginaesanguine.domain.helpers.B2
import br.com.gabryel.reginaesanguine.domain.helpers.B5
import br.com.gabryel.reginaesanguine.domain.helpers.C1
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.matchers.cardCellWith
import br.com.gabryel.reginaesanguine.domain.matchers.emptyCellOwnedBy
import br.com.gabryel.reginaesanguine.domain.matchers.haveCell
import br.com.gabryel.reginaesanguine.domain.matchers.haveCellTotalPower
import br.com.gabryel.reginaesanguine.domain.matchers.haveCells
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeFailure
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccess
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccessfulAnd
import br.com.gabryel.reginaesanguine.domain.util.buildResult
import io.kotest.matchers.maps.containExactly
import io.kotest.matchers.should
import kotlin.test.Test

class BoardEffectTest {
    @Test
    fun `when a LaneBonus effect is up, should add then to getScores`() {
        val laneBonus = cardOf("Lane Bonus", power = 8, effect = RaiseWinnerLanesByLoserScore())

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(A1, laneBonus)).orRaiseError().board
                .play(LEFT, Play(B1, cardOf(power = 20))).orRaiseError().board
                .play(RIGHT, Play(A5, cardOf(power = 2))).orRaiseError().board
                .play(RIGHT, Play(B5, cardOf(power = 80))).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 10,
            RIGHT to 100,
        )
    }

    @Test
    fun `when playing a card with RaiseRank effect, should increment rank on increment positions by specified amount`() {
        val cardWithRaiseRank = cardOf(
            "Rank Raiser",
            increments = setOf(RIGHTWARD, UPWARD),
            effect = RaiseRankDefault(2),
        )

        val nextBoard = Board.default()
            .play(LEFT, Play(B1, cardWithRaiseRank))
            .map { it.board }

        nextBoard shouldBeSuccessfulAnd haveCells(
            (A1) to emptyCellOwnedBy(LEFT, 3),
            (B2) to emptyCellOwnedBy(LEFT, 2),
        )
    }

    @Test
    fun `when playing a card with Raisable effect, should increment rank on increment positions by specified amount`() {
        val cardWithRaiseRank = raisePowerCard(LEFTWARD, 1, trigger = WhileActive)

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(A1, SECURITY_OFFICER)).orRaiseError().board
                .play(LEFT, Play(A2, cardWithRaiseRank)).orRaiseError().board
        }

        nextBoard shouldBeSuccessfulAnd haveCellTotalPower(A1, 2)
    }

    @Test
    fun `when a card with DestroyEntity effect is played, should remove target card`() {
        val destroyUp = destroyerCard(destroyEffect = UPWARD, target = ALLIES, trigger = WhenPlayed())

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, SECURITY_OFFICER)).orRaiseError().board
                .play(LEFT, Play(C1, destroyUp)).orRaiseError().board
        }

        nextBoard shouldBeSuccessfulAnd haveCell(B5, emptyCellOwnedBy(RIGHT, 1))
    }

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

        val replaceEffect = ReplaceAllyRaise(powerMultiplier = 1, ANY, affected = setOf(DOWNWARD))
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

    private fun raisePowerCard(
        raiseEffect: Displacement,
        powerRaise: Int,
        target: TargetType = ALLIES,
        trigger: Trigger = WhenPlayed()
    ): Card = cardOf(
        "Raise Power ($target at $raiseEffect $trigger)",
        effect = RaisePower(powerRaise, target, trigger, affected = setOf(raiseEffect)),
    )

    private fun destroyerCard(
        destroyEffect: Displacement,
        target: TargetType = ENEMIES,
        trigger: Trigger = WhenPlayed()
    ): Card = cardOf(
        "Card Destroyer ($target at $destroyEffect $trigger)",
        effect = DestroyCardsDefault(target, trigger, affected = setOf(destroyEffect)),
    )
}
