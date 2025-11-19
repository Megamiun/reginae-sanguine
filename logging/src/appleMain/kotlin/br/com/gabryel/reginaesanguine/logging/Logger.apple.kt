package br.com.gabryel.reginaesanguine.logging

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLog
import kotlin.reflect.KClass

@OptIn(ExperimentalForeignApi::class)
actual class Logger actual constructor(private val loggerName: String) {
    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())

    actual fun error(text: Any?, exception: Throwable?) {
        log(text, exception, "ERROR")
    }

    actual fun info(text: Any?, exception: Throwable?) {
        log(text, exception, "INFO")
    }

    private fun log(text: Any?, exception: Throwable?, level: String) {
        val message = if (exception == null) text else "$text - Exception: $exception"

        NSLog("$level [$loggerName]: $message")
    }
}
