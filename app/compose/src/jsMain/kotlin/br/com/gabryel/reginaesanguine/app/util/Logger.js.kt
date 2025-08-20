package br.com.gabryel.reginaesanguine.app.util

import kotlin.reflect.KClass

actual class Logger actual constructor(loggerName: String) {
    actual fun error(text: Any?, exception: Throwable?) {
        log(text, exception, console::error)
    }

    actual fun info(text: Any?, exception: Throwable?) {
        log(text, exception, console::info)
    }

    private fun log(text: Any?, exception: Throwable? = null, logger: (Array<Any?>) -> Unit) {
        if (exception == null)
            logger(arrayOf(text))
        else
            logger(arrayOf(text, exception))
    }

    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())
}
