package br.com.gabryel.reginarsanguine.domain.matchers

import br.com.gabryel.reginarsanguine.domain.Failure
import br.com.gabryel.reginarsanguine.domain.Result
import br.com.gabryel.reginarsanguine.domain.Success
import io.kotest.matchers.Matcher
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

fun <T> Result<T>.shouldBeSuccess() =
    shouldBeInstanceOf<Success<T>>().value

infix fun <T> Result<T>.shouldBeSuccessfulAnd(match: Matcher<T>) =
    shouldBeInstanceOf<Success<T>>().value shouldBe match

infix fun <T> Result<T>.shouldBeSuccessfulAndHave(match: Matcher<T>) = shouldBeSuccessfulAnd(match)

inline fun <reified FAIL: Failure> Result<*>.shouldBeFailure() =
    shouldBeInstanceOf<FAIL>()