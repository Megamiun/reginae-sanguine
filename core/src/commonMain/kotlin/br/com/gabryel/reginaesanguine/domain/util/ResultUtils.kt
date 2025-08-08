@file:JvmName("ResultUtils")
@file:JvmMultifileClass

package br.com.gabryel.reginaesanguine.domain.util

import arrow.core.raise.Raise
import arrow.core.raise.recover
import br.com.gabryel.reginaesanguine.domain.Failure
import br.com.gabryel.reginaesanguine.domain.Failure.UnknownError
import br.com.gabryel.reginaesanguine.domain.Result
import br.com.gabryel.reginaesanguine.domain.Success
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@JvmInline
value class ResultRaise<A>(private val raise: Raise<Failure>) : Raise<Failure> by raise {
    fun <B> Result<B>.orRaiseError(): B =
        when (this) {
            is Success<B> -> value
            is Failure -> raise(this)
        }
}

inline fun <A> buildResult(run: ResultRaise<A>.() -> A): Result<A> = try {
    recover({ Success(run(ResultRaise(this))) }) { it }
} catch (err: Throwable) {
    // TODO There must be a better solution
    UnknownError(err)
}
