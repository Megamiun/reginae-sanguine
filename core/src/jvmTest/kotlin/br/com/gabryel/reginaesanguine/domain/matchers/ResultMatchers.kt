package br.com.gabryel.reginaesanguine.domain.matchers

import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.Success
import br.com.gabryel.reginaesanguine.domain.util.ResultRaise
import br.com.gabryel.reginaesanguine.domain.util.buildResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeInstanceOf

fun <T> Result<T>.shouldBeSuccess() = shouldBeInstanceOf<Success<T>>().value

infix fun <T> Result<T>.shouldBeSuccessfulAnd(match: Matcher<T>) = shouldBeInstanceOf<Success<T>>().value shouldBe match

inline fun <reified FAIL : Failure> Result<*>.shouldBeFailure() = shouldBeInstanceOf<FAIL>()

inline fun <reified FAIL : Failure> shouldFailWith(exec: ResultRaise<*>.() -> Result<*>) =
    buildResult(exec).flatmap { it }.shouldBeFailure<FAIL>()
