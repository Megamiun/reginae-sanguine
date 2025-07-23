package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Action.Play
import br.com.gabryel.reginaesanguine.domain.Action.Skip
import br.com.gabryel.reginaesanguine.domain.Failure.GameEnded
import br.com.gabryel.reginaesanguine.domain.Failure.NotPlayerTurn
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.LEFT
import br.com.gabryel.reginaesanguine.domain.PlayerPosition.RIGHT
import br.com.gabryel.reginaesanguine.domain.State.Ended
import br.com.gabryel.reginaesanguine.domain.helpers.LEFT_COLUMN
import br.com.gabryel.reginaesanguine.domain.helpers.MIDDLE_LANE
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.RIOT_TROOPER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.matchers.cardCellWith
import br.com.gabryel.reginaesanguine.domain.matchers.haveCardsAtDeck
import br.com.gabryel.reginaesanguine.domain.matchers.haveCardsAtHand
import br.com.gabryel.reginaesanguine.domain.matchers.haveCell
import br.com.gabryel.reginaesanguine.domain.matchers.havePlayerOn
import br.com.gabryel.reginaesanguine.domain.matchers.satisfyAll
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeFailure
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccess
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccessfulAnd
import br.com.gabryel.reginaesanguine.domain.matchers.shouldFailWith
import br.com.gabryel.reginaesanguine.domain.util.buildResult
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.should
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

class GameTest {
    @Test
    fun `when playing a card on a valid position, should add player card to position`() {
        val nextTurn = defaultGame()
            .play(LEFT, Play(1 to 0, SECURITY_OFFICER.id))

        nextTurn shouldBeSuccessfulAnd haveCell(1 to 0, cardCellWith(LEFT, SECURITY_OFFICER))
    }

    @Test
    fun `when playing a card, should change player`() {
        val nextTurn = defaultGame()
            .play(LEFT, Play(1 to 0, SECURITY_OFFICER.id))

        nextTurn
            .shouldBeSuccess()
            .nextPlayer.shouldBeEqual(RIGHT)
    }

    @Test
    fun `when playing a card as the wrong player, should fail with NotPlayerTurn`() {
        val nextTurn = defaultGame()
            .play(RIGHT, Play(1 to 0, SECURITY_OFFICER.id))

        nextTurn.shouldBeFailure<NotPlayerTurn>()
    }

    @Test
    fun `when playing a card, should register last action to previous turn`() {
        val action = Play(1 to 0, SECURITY_OFFICER.id)
        val nextTurn = defaultGame()
            .play(LEFT, action)

        nextTurn
            .shouldBeSuccess()
            .previous?.action?.shouldBeEqual(action)
    }

    @Test
    fun `when making two consecutive skips, should end game`() {
        val nextTurn = buildResult {
            defaultGame()
                .play(LEFT, Skip).orRaiseError()
                .play(RIGHT, Skip).orRaiseError()
        }

        nextTurn
            .shouldBeSuccess()
            .getState().shouldBeInstanceOf<Ended>()
    }

    @Test
    fun `when making a move after a game end, should fail with GameEnded`() {
        shouldFailWith<GameEnded> {
            defaultGame()
                .play(LEFT, Skip).orRaiseError()
                .play(RIGHT, Skip).orRaiseError()
                .play(LEFT, Skip)
        }
    }

    @Test
    fun `when a player makes a move, the other player should draw a new card`() {
        val nextTurn = buildResult {
            defaultGame().play(LEFT, Skip).orRaiseError()
        }

        nextTurn.shouldBeSuccess().havePlayerOn(RIGHT) should satisfyAll(
            haveCardsAtHand(SECURITY_OFFICER, RIOT_TROOPER),
            haveCardsAtDeck(),
        )
    }

    @Test
    fun `when a player makes a move, the player should lose the given card`() {
        val nextTurn = buildResult {
            defaultGame().play(LEFT, Play(MIDDLE_LANE to LEFT_COLUMN, SECURITY_OFFICER.id)).orRaiseError()
        }

        nextTurn.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(RIOT_TROOPER)
    }

    @Test
    fun `when the game starts, both players should start with defined amount of cards drawn`() {
        val nextTurn = buildResult {
            val defaultPlayer = Player(deck = (1..10).map { RIOT_TROOPER })
            Game.forPlayers(defaultPlayer, defaultPlayer, 2)
        }

        nextTurn.shouldBeSuccess().havePlayerOn(LEFT) should haveCardsAtHand(RIOT_TROOPER, RIOT_TROOPER)
        nextTurn.shouldBeSuccess().havePlayerOn(RIGHT) should haveCardsAtHand(RIOT_TROOPER, RIOT_TROOPER)
    }

    private fun defaultGame(): Game {
        val defaultPlayer = Player(listOf(SECURITY_OFFICER), listOf(RIOT_TROOPER))
        return Game.forPlayers(defaultPlayer, defaultPlayer)
    }
}
