package br.com.gabryel.reginarsanguine.domain

import io.kotest.matchers.Matcher
import io.kotest.matchers.compose.all
import io.kotest.matchers.equals.beEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun <T> Result<T>.shouldBeSuccess() =
    shouldBeInstanceOf<Success<T>>().value

fun <T> Result<T>.shouldBeSuccessAndBe(match: Matcher<T>) =
    shouldBeInstanceOf<Success<T>>().value shouldBe match

inline fun <reified FAIL: Failure> Result<*>.shouldBeFailure() =
    shouldBeInstanceOf<FAIL>()

fun cellWith(owner: PlayerPosition, card: Card) = Matcher.all(
    Cell::owner shouldMatch beEqual(owner),
    Cell::card shouldMatch beEqual(card),
)
