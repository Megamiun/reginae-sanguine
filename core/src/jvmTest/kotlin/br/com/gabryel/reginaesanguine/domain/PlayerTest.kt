package br.com.gabryel.reginaesanguine.domain

import br.com.gabryel.reginaesanguine.domain.Failure.CardNotOnHand
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.RIOT_TROOPER
import br.com.gabryel.reginaesanguine.domain.helpers.SampleCards.SECURITY_OFFICER
import br.com.gabryel.reginaesanguine.domain.matchers.haveCardsAtHand
import br.com.gabryel.reginaesanguine.domain.matchers.shouldBeSuccessfulAnd
import br.com.gabryel.reginaesanguine.domain.matchers.shouldFailWith
import kotlin.test.Test

class PlayerTest {
    @Test
    fun `when player plays card on hand, should return player without given card`() {
        val afterPlay = Player(listOf(SECURITY_OFFICER, RIOT_TROOPER))
            .selectCard(SECURITY_OFFICER.id)

        afterPlay.map { it.first } shouldBeSuccessfulAnd haveCardsAtHand(RIOT_TROOPER)
    }

    @Test
    fun `when player plays card that has multiple copies on hand, should return player without one instance of given card`() {
        val afterPlay = Player(listOf(SECURITY_OFFICER, SECURITY_OFFICER, RIOT_TROOPER, SECURITY_OFFICER))
            .selectCard(SECURITY_OFFICER.id)

        afterPlay.map { it.first } shouldBeSuccessfulAnd haveCardsAtHand(SECURITY_OFFICER, RIOT_TROOPER, SECURITY_OFFICER)
    }

    @Test
    fun `when player plays card not on hand, should fail with CardNotOnHand`() {
        shouldFailWith<CardNotOnHand> {
            Player(listOf(SECURITY_OFFICER)).selectCard(RIOT_TROOPER.id)
        }
    }
}
