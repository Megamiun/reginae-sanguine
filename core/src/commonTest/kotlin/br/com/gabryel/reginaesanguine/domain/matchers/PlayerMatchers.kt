package br.com.gabryel.reginaesanguine.domain.matchers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Player
import io.kotest.matchers.collections.containExactly

fun haveCardsAtHand(vararg cards: Card) = Player::hand shouldMatch containExactly(*cards)

fun haveCardsAtDeck(vararg cards: Card) = Player::deck shouldMatch containExactly(*cards)
