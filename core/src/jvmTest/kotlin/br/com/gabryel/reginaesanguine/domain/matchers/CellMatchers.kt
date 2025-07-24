package br.com.gabryel.reginaesanguine.domain.matchers

import br.com.gabryel.reginaesanguine.domain.Card
import br.com.gabryel.reginaesanguine.domain.Cell
import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import io.kotest.matchers.Matcher
import io.kotest.matchers.compose.all
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.nulls.beNull

fun unclaimedCell() =
    Matcher.all(
        Cell::owner shouldMatch beNull(),
        Cell::rank shouldMatch beEqual(0),
        Cell::card shouldMatch beNull(),
    )

fun emptyCellOwnedBy(
    owner: PlayerPosition,
    pins: Int,
) = Matcher.all(
    Cell::owner shouldMatch beEqual(owner),
    Cell::rank shouldMatch beEqual(pins),
    Cell::card shouldMatch beNull(),
)

fun cardCellWith(
    owner: PlayerPosition,
    card: Card,
) = Matcher.all(
    Cell::owner shouldMatch beEqual(owner),
    Cell::card shouldMatch beEqual(card),
)

fun cardCellWithTotalPower(
    power: Int,
) = Cell::totalPower shouldMatch beEqual(power)

fun cardCellWith(
    owner: PlayerPosition,
    card: Card,
    pins: Int,
) = Matcher.all(
    Cell::owner shouldMatch beEqual(owner),
    Cell::rank shouldMatch beEqual(pins),
    Cell::card shouldMatch beEqual(card),
)
