package br.com.gabryel.reginarsanguine.domain

import io.kotest.matchers.Matcher
import io.kotest.matchers.compose.all
import io.kotest.matchers.equals.beEqual

fun beCellWith(owner: PlayerPosition, card: Card) = Matcher.all(
    Cell::owner shouldMatch beEqual(owner),
    Cell::card shouldMatch beEqual(card),
)