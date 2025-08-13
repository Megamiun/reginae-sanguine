package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.LEFTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.RIGHTWARD
import br.com.gabryel.reginaesanguine.domain.Displacement.Companion.UPWARD
import br.com.gabryel.reginaesanguine.domain.Failure.CellDoesNotBelongToPlayer
import br.com.gabryel.reginaesanguine.domain.Failure.CellOccupied
import br.com.gabryel.reginaesanguine.domain.Failure.CellOutOfBoard
import br.com.gabryel.reginaesanguine.domain.Failure.CellRankLowerThanCard
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ENEMIES
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import br.com.gabryel.reginaesanguine.domain.effect.type.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.type.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.type.RaiseRank
import br.com.gabryel.reginaesanguine.domain.effect.type.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.helpers.A1
import br.com.gabryel.reginaesanguine.domain.helpers.A2
import br.com.gabryel.reginaesanguine.domain.helpers.A5
import br.com.gabryel.reginaesanguine.domain.helpers.B1
import br.com.gabryel.reginaesanguine.domain.helpers.B2
import br.com.gabryel.reginaesanguine.domain.helpers.B3
import br.com.gabryel.reginaesanguine.domain.helpers.B4
import br.com.gabryel.reginaesanguine.domain.helpers.B5
import br.com.gabryel.reginaesanguine.domain.helpers.C1
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.RIOT_TROOPER
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
import br.com.gabryel.reginaesanguine.domain.matchers.unclaimedCell
import br.com.gabryel.reginaesanguine.domain.util.buildResult
import io.kotest.matchers.maps.containExactly
import io.kotest.matchers.should
import kotlin.test.Test

class BoardTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(B1, SECURITY_OFFICER))
            .map { it.board }

        nextBoard shouldBeSuccessfulAnd haveCell(B1, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card on a position where you have no enough rank, should fail with CellRankLowerThanCard`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(B1, RIOT_TROOPER))
            .map { it.board }

        nextBoard.shouldBeFailure<CellRankLowerThanCard>()
    }

    @Test
    fun `when playing a card on a position you have no control, should fail with CellDoesNotBelongToPlayer`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(B1, SECURITY_OFFICER))
            .map { it.board }

        nextBoard.shouldBeFailure<CellDoesNotBelongToPlayer>()
    }

    @Test
    fun `when playing a card on a position outside the board, should fail with OutOfBoard`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(-1 atColumn -1, SECURITY_OFFICER))
            .map { it.board }

        nextBoard.shouldBeFailure<CellOutOfBoard>()
    }

    @Test
    fun `when playing a card on a position where you already have a card, should fail with CellOccupied`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, SECURITY_OFFICER)).orRaiseError().board
                .play(LEFT, Play(B1, SECURITY_OFFICER)).orRaiseError().board
        }

        nextBoard.shouldBeFailure<CellOccupied>()
    }

    @Test
    fun `when playing a card, should increment rank on all increment positions described in the cards`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(B1, SECURITY_OFFICER))
            .map { it.board }

        nextBoard shouldBeSuccessfulAnd haveCells(
            C1 to emptyCellOwnedBy(LEFT, 2),
            A1 to emptyCellOwnedBy(LEFT, 2),
            B2 to emptyCellOwnedBy(LEFT, 1),
            // Some extras for security
            A2 to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card as RIGHT player, should increment rank on all mirrored increment positions described in the cards`() {
        val powerRaise = cardOf("Only Increment Right", increments = setOf(RIGHTWARD))

        val nextBoard = Board.default()
            .copy(state = mapOf(B3 to Cell(RIGHT, 1)))
            .play(RIGHT, Play(B3, powerRaise))
            .map { it.board }

        nextBoard shouldBeSuccessfulAnd haveCells(
            B2 to emptyCellOwnedBy(RIGHT, 1),
            // Some extras for security
            B4 to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card, should add points player score`() {
        val smallCard = cardOf(name = "ADD4", power = 4)
        val bigCard = cardOf(name = "ADD5", power = 5)
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, smallCard)).orRaiseError().board
                .play(RIGHT, Play(A5, bigCard)).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 4,
            RIGHT to 5,
        )
    }

    @Test
    fun `given two players have different amount of points in same lane, when retrieving scores, only consider greatest score`() {
        val smallCard = cardOf(name = "ADD4", power = 4)
        val bigCard = cardOf(name = "ADD5", power = 5)

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, smallCard)).orRaiseError().board
                .play(RIGHT, Play(B5, bigCard)).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 5,
        )
    }

    @Test
    fun `when a player wins a lane with a ScoreBonus card, should receive bonus points`() {
        val laneBonus = cardOf("Lane Bonus", effect = ScoreBonus(5))

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, laneBonus)).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 6,
            RIGHT to 0,
        )
    }

    @Test
    fun `when a player loses a lane with a ScoreBonus card, should not receive bonus points`() {
        val laneBonus = cardOf("Lane Bonus", effect = ScoreBonus(5))

        val strongCard = cardOf(name = "Strong", power = 5)
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, laneBonus)).orRaiseError().board
                .play(RIGHT, Play(B5, strongCard)).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 5,
        )
    }

    @Test
    fun `when two players have equal power in same lane, both should get 0 points`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(B1, SECURITY_OFFICER)).orRaiseError().board
                .play(RIGHT, Play(B5, SECURITY_OFFICER)).orRaiseError().board
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 0,
        )
    }

    @Test
    fun `when playing a card with RaiseRank effect, should increment rank on increment positions by specified amount`() {
        val cardWithRaiseRank = cardOf(
            "Rank Raiser",
            increments = setOf(RIGHTWARD, UPWARD),
            effect = RaiseRank(2),
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
        effect = DestroyCards(target, trigger, affected = setOf(destroyEffect)),
    )
}
