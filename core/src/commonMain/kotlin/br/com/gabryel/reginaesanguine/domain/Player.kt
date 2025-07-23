package br.com.gabryel.reginaesanguine.domain

import arrow.core.raise.ensure
import br.com.gabryel.reginaesanguine.domain.Failure.CardNotOnHand
import br.com.gabryel.reginaesanguine.domain.util.buildResult

data class Player(val hand: List<Card> = listOf(), val deck: List<Card> = listOf()) {
    fun selectCard(cardId: String): Result<Pair<Player, Card>> = buildResult {
        val cardPosition = hand.indexOfFirst { card -> card.id == cardId }

        ensure(cardPosition != -1) { CardNotOnHand(cardId) }

        copy(hand = hand.filterIndexed { index, _ -> index != cardPosition }) to hand[cardPosition]
    }

    fun draw(amount: Int = 1): Player = when {
        amount == 0 || deck.isEmpty() -> this
        else -> copy(hand = hand + deck.first(), deck = deck.drop(1)).draw(amount - 1)
    }
}
