@file:JvmName("ResultUtils")
@file:JvmMultifileClass

package br.com.gabryel.reginarsanguine.util

import arrow.core.raise.Raise
import arrow.core.raise.recover
import br.com.gabryel.reginarsanguine.domain.Failure
import br.com.gabryel.reginarsanguine.domain.Result
import br.com.gabryel.reginarsanguine.domain.Success

@JvmInline
value class ResultRaise<A>(private val raise: Raise<Failure>) : Raise<Failure> by raise {
    fun <B> Result<B>.orRaiseError(): B =
        when (this) {
            is Success<B> -> value
            is Failure -> raise(this)
        }
}

inline fun <A> buildResult(run: ResultRaise<A>.() -> A): Result<A> = recover({ Success(run(ResultRaise(this))) }) { it }
