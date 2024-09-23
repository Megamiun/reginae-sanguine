package br.com.gabryel.reginarsanguine.domain

import br.com.gabryel.reginarsanguine.domain.Action.Play
import br.com.gabryel.reginarsanguine.domain.Action.Skip
import br.com.gabryel.reginarsanguine.domain.Failure.*
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginarsanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginarsanguine.domain.State.Ended
import br.com.gabryel.reginarsanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginarsanguine.domain.matchers.*
import br.com.gabryel.reginarsanguine.util.buildResult
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class GameTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextTurn =
            Game.default()
                .play(LEFT, Play(1 to 0, SECURITY_OFFICER))

        nextTurn shouldBeSuccessfulAnd haveCell(1 to 0, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card, should change player`() {
        val nextTurn =
            Game.default()
                .play(LEFT, Play(1 to 0, SECURITY_OFFICER))

        nextTurn
            .shouldBeSuccess()
            .nextPlayer.shouldBeEqual(RIGHT)
    }

    @Test
    fun `when playing a card as the wrong player, should fail with NotPlayerTurn`() {
        val nextTurn =
            Game.default()
                .play(RIGHT, Play(1 to 0, SECURITY_OFFICER))

        nextTurn.shouldBeFailure<NotPlayerTurn>()
    }

    @Test
    fun `when playing a card, should register last action to previous turn`() {
        val action = Play(1 to 0, SECURITY_OFFICER)
        val nextTurn =
            Game.default()
                .play(LEFT, action)

        nextTurn
            .shouldBeSuccess()
            .previous?.action?.shouldBeEqual(action)
    }

    @Test
    fun `when making two consecutive skips, should end game`() {
        val nextTurn =
            buildResult {
                Game.default()
                    .play(LEFT, Skip).orRaiseError()
                    .play(RIGHT, Skip).orRaiseError()
            }

        nextTurn
            .shouldBeSuccess()
            .getState().shouldBeInstanceOf<Ended>()
    }

    // TODO given player does not have the card in hand, when playing same card, should fail with CardUnavailable
    // TODO when first player makes a move, second player should draw a new card
}
