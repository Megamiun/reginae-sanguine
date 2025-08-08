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
import br.com.gabryel.reginaesanguine.domain.effect.DestroyCards
import br.com.gabryel.reginaesanguine.domain.effect.RaisePower
import br.com.gabryel.reginaesanguine.domain.effect.RaiseRank
import br.com.gabryel.reginaesanguine.domain.effect.ScoreBonus
import br.com.gabryel.reginaesanguine.domain.effect.TargetType
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ALLIES
import br.com.gabryel.reginaesanguine.domain.effect.TargetType.ENEMIES
import br.com.gabryel.reginaesanguine.domain.effect.Trigger
import br.com.gabryel.reginaesanguine.domain.effect.WhenLaneWon
import br.com.gabryel.reginaesanguine.domain.effect.WhenPlayed
import br.com.gabryel.reginaesanguine.domain.effect.WhileActive
import br.com.gabryel.reginaesanguine.domain.helpers.BOTTOM_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.CENTER_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.CENTER_LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.CENTER_RIGHT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.RIGHT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.RIOT_TROOPER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.helpers.TOP_LANE
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
            .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd haveCell(MIDDLE_LANE atColumn LEFT_COLUMN, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card on a position where you have no enough rank, should fail with NotEnoughrank`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, RIOT_TROOPER))

        nextBoard.shouldBeFailure<CellRankLowerThanCard>()
    }

    @Test
    fun `when playing a card on a position you have no control, should fail with CellDoesNotBelongToPlayer`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<CellDoesNotBelongToPlayer>()
    }

    @Test
    fun `when playing a card on a position outside the board, should fail with OutOfBoard`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(-1 atColumn -1, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<CellOutOfBoard>()
    }

    @Test
    fun `when playing a card on a position where you already have a card, should fail with CellOccupied`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
        }

        nextBoard.shouldBeFailure<CellOccupied>()
    }

    @Test
    fun `when playing a card, should increment rank on all increment positions described in the cards`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd haveCells(
            (BOTTOM_LANE atColumn LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 2),
            (TOP_LANE atColumn LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 2),
            (MIDDLE_LANE atColumn CENTER_LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 1),
            // Some extras for security
            (TOP_LANE atColumn CENTER_LEFT_COLUMN) to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card as RIGHT player, should increment rank on all mirrored increment positions described in the cards`() {
        val powerRaise = cardOf(
            "Only Increment Right",
            increments = setOf(RIGHTWARD),
        )

        val nextBoard = Board.default()
            .copy(state = mapOf((MIDDLE_LANE atColumn CENTER_COLUMN) to Cell(RIGHT, 1)))
            .play(RIGHT, Play(MIDDLE_LANE atColumn CENTER_COLUMN, powerRaise))

        nextBoard shouldBeSuccessfulAnd haveCells(
            (MIDDLE_LANE atColumn CENTER_LEFT_COLUMN) to emptyCellOwnedBy(RIGHT, 1),
            // Some extras for security
            (MIDDLE_LANE atColumn CENTER_RIGHT_COLUMN) to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card, should add points player score`() {
        val smallCard = cardOf(name = "ADD4", value = 4)
        val bigCard = cardOf(name = "ADD5", value = 5)
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, smallCard)).orRaiseError()
                .play(RIGHT, Play(TOP_LANE atColumn RIGHT_COLUMN, bigCard)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 4,
            RIGHT to 5,
        )
    }

    @Test
    fun `given two players have different amount of points in same lane, when retrieving scores, only consider greatest score`() {
        val smallCard = cardOf(name = "ADD4", value = 4)
        val bigCard = cardOf(name = "ADD5", value = 5)

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, smallCard)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, bigCard)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 5,
        )
    }

    @Test
    fun `when a card with RaisePower effects is played, should add effect to affected entities`() {
        val powerRaise = rasePowerCard(LEFTWARD, 2)

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE atColumn CENTER_LEFT_COLUMN, powerRaise)).orRaiseError()
        }

        nextBoard shouldBeSuccessfulAnd
            haveCellTotalPower(MIDDLE_LANE atColumn LEFT_COLUMN, 3)
    }

    @Test
    fun `when a player wins a lane with a ScoreBonus card with OnLaneWon, should receive bonus points`() {
        val laneBonus = cardOf("Lane Bonus", effect = ScoreBonus(5, WhenLaneWon))

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, laneBonus)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 6,
            RIGHT to 0,
        )
    }

    @Test
    fun `when a player loses a lane with a ScoreBonus card with OnLaneWon, should not receive bonus points`() {
        val laneBonus = cardOf("Lane Bonus", effect = ScoreBonus(5, WhenLaneWon))

        val strongCard = cardOf(name = "Strong", value = 5)
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, laneBonus)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, strongCard)).orRaiseError()
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
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, SECURITY_OFFICER)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 0,
        )
    }

    @Test
    fun `when a card with DestroyEntity effect is played, should remove target card`() {
        val destroyUp = destroyerCard(destroyEffect = UPWARD, target = ALLIES, trigger = WhenPlayed())

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(BOTTOM_LANE atColumn LEFT_COLUMN, destroyUp)).orRaiseError()
        }

        nextBoard shouldBeSuccessfulAnd haveCell(MIDDLE_LANE atColumn RIGHT_COLUMN, emptyCellOwnedBy(RIGHT, 1))
    }

    @Test
    fun `when a card with a effect is played by RIGHT player, should apply mirrored effect to affected entities`() {
        val powerRaise = rasePowerCard(LEFTWARD, power = 2)

        val nextBoard = buildResult {
            Board.default()
                .play(RIGHT, Play(MIDDLE_LANE atColumn RIGHT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE atColumn CENTER_RIGHT_COLUMN, powerRaise)).orRaiseError()
        }

        nextBoard shouldBeSuccessfulAnd
            haveCellTotalPower(MIDDLE_LANE atColumn RIGHT_COLUMN, 3)
    }

    @Test
    fun `when playing a card with RaiseRank effect, should increment rank on increment positions by specified amount`() {
        val cardWithRaiseRank = cardOf(
            "Rank Raiser",
            increments = setOf(RIGHTWARD, UPWARD),
            effect = RaiseRank(2),
        )

        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, cardWithRaiseRank))

        nextBoard shouldBeSuccessfulAnd haveCells(
            (TOP_LANE atColumn LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 3),
            (MIDDLE_LANE atColumn CENTER_LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 2),
        )
    }

    @Test
    fun `when a card with WhileActive RaisePower is destroyed, should remove power bonus from target`() {
        val whileActivePowerCard = rasePowerCard(UPWARD, power = 2, trigger = WhileActive)
        val destroyUp = destroyerCard(UPWARD, ALLIES)

        val board = buildResult {
            Board.default()
                .play(LEFT, Play(TOP_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, whileActivePowerCard)).orRaiseError()
                .play(LEFT, Play(BOTTOM_LANE atColumn LEFT_COLUMN, destroyUp)).orRaiseError()
        }

        board shouldBeSuccessfulAnd haveCellTotalPower(TOP_LANE atColumn LEFT_COLUMN, 1)
    }

    @Test
    fun `when a card with WhenPlayed RaisePower is destroyed, should keep power bonus on target`() {
        val raiseUpWhenPlayed = rasePowerCard(UPWARD, power = 2)
        val destroyUp = destroyerCard(UPWARD, ALLIES)

        val board = buildResult {
            Board.default()
                .play(LEFT, Play(TOP_LANE atColumn LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE atColumn LEFT_COLUMN, raiseUpWhenPlayed)).orRaiseError()
                .play(LEFT, Play(BOTTOM_LANE atColumn LEFT_COLUMN, destroyUp)).orRaiseError()
        }

        board shouldBeSuccessfulAnd haveCellTotalPower(TOP_LANE atColumn LEFT_COLUMN, 3)
    }

    private fun rasePowerCard(
        raiseEffect: Displacement,
        power: Int,
        target: TargetType = ALLIES,
        trigger: Trigger = WhenPlayed()
    ): Card = cardOf(
        "Raise Power ($target at $raiseEffect $trigger)",
        effect = RaisePower(power, target, trigger, affected = setOf(raiseEffect)),
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
