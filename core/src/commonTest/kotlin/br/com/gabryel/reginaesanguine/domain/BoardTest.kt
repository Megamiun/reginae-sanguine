package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Failure.*
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.helpers.BOTTOM_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.CENTER_LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.CENTER_RIGHT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.RIGHT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.CRYSTALLINE_CRAB
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.RIOT_TROOPER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.cardOf
import br.com.gabryel.reginaesanguine.domain.helpers.TOP_LANE
import br.com.gabryel.reginaesanguine.domain.matchers.*
import br.com.gabryel.reginaesanguine.util.buildResult
import io.kotest.matchers.maps.containExactly
import io.kotest.matchers.should
import kotlin.test.Test

class BoardTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd haveCell(MIDDLE_LANE to LEFT_COLUMN, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card on a position where you have no enough pins, should fail with NotEnoughPins`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, RIOT_TROOPER))

        nextBoard.shouldBeFailure<NotEnoughPins>()
    }

    @Test
    fun `when playing a card on a position you have no control, should fail with CellDoesNotBelongToPlayer`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<CellDoesNotBelongToPlayer>()
    }

    @Test
    fun `when playing a card on a position outside the board, should fail with OutOfBoard`() {
        val nextBoard = Board.default()
            .play(RIGHT, Play(-1 to -1, SECURITY_OFFICER))

        nextBoard.shouldBeFailure<OutOfBoard>()
    }

    @Test
    fun `when playing a card on a position where you already have a card, should fail with CellOccupied`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE to RIGHT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
        }

        nextBoard.shouldBeFailure<CellOccupied>()
    }

    @Test
    fun `when playing a card, should increment pins on all increment positions described in the cards`() {
        val nextBoard = Board.default()
            .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER))

        nextBoard shouldBeSuccessfulAnd haveCells(
            (BOTTOM_LANE to LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 2),
            (TOP_LANE to LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 2),
            (MIDDLE_LANE to CENTER_LEFT_COLUMN) to emptyCellOwnedBy(LEFT, 1),
            // Some extras for security
            (TOP_LANE to CENTER_LEFT_COLUMN) to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card as RIGHT player, should increment pins on all mirrored increment positions described in the cards`() {
        val nextBoard = Board.default().copy(state = mapOf((MIDDLE_LANE to CENTER_RIGHT_COLUMN) to Cell(RIGHT)))
            .play(RIGHT, Play(MIDDLE_LANE to CENTER_RIGHT_COLUMN, CRYSTALLINE_CRAB))

        nextBoard shouldBeSuccessfulAnd haveCells(
            (BOTTOM_LANE to CENTER_RIGHT_COLUMN) to emptyCellOwnedBy(RIGHT, 2),
            (TOP_LANE to CENTER_RIGHT_COLUMN) to emptyCellOwnedBy(RIGHT, 1),
            (MIDDLE_LANE to RIGHT_COLUMN) to emptyCellOwnedBy(RIGHT, 1),
            // Some extras for security
            (TOP_LANE to RIGHT_COLUMN) to unclaimedCell(),
        )
    }

    @Test
    fun `when playing a card, should add points player score`() {
        val smallCard = cardOf(name = "ADD4", value = 4)
        val bigCard = cardOf(name = "ADD5", value = 5)
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, smallCard)).orRaiseError()
                .play(RIGHT, Play(TOP_LANE to RIGHT_COLUMN, bigCard)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 4,
            RIGHT to 5,
        )
    }

    @Test
    fun `given two players have different amount of points in same row, when retrieving scores, only consider greatest score`() {
        val smallCard = cardOf(name = "ADD4", value = 4)
        val bigCard = cardOf(name = "ADD5", value = 5)

        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, smallCard)).orRaiseError()
                .play(RIGHT, Play(MIDDLE_LANE to RIGHT_COLUMN, bigCard)).orRaiseError()
        }

        nextBoard.shouldBeSuccess().getScores() should containExactly(
            LEFT to 0,
            RIGHT to 5,
        )
    }

    @Test
    fun `when a card with power raise effects is played, should add effect to affected entities`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE to CENTER_LEFT_COLUMN, CRYSTALLINE_CRAB)).orRaiseError()
        }

        nextBoard shouldBeSuccessfulAnd
                haveCell(MIDDLE_LANE to LEFT_COLUMN, cardCellWithTotalPower(3))
    }

    @Test
    fun `when a card with power raise effects is played by RIGHT player, should apply mirrored effect to affected entities`() {
        val nextBoard = buildResult {
            Board.default()
                .play(LEFT, Play(MIDDLE_LANE to RIGHT_COLUMN, SECURITY_OFFICER)).orRaiseError()
                .play(LEFT, Play(MIDDLE_LANE to CENTER_RIGHT_COLUMN, CRYSTALLINE_CRAB)).orRaiseError()
        }

        nextBoard shouldBeSuccessfulAnd
                haveCell(MIDDLE_LANE to RIGHT_COLUMN, cardCellWithTotalPower(3))
    }
}
