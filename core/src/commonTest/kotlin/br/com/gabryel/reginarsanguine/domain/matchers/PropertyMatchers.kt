package br.com.gabryel.reginarsanguine.domain.matchers

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.all
import kotlin.reflect.KProperty1

infix fun <T, V> KProperty1<T, V>.shouldMatch(matcher: Matcher<V>) =
    Matcher<T> { value ->
        val property = this@shouldMatch
        val result = matcher.test(property(value))

        MatcherResult(
            result.passed(),
            { "${property.name}: ${result.failureMessage()}" },
            { "${property.name}: ${result.negatedFailureMessage()}" },
        )
    }

fun <T> satisfyAll(vararg matchers: Matcher<T>) =
    Matcher.all(*matchers)
