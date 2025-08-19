package br.com.gabryel.reginaesanguine.app.util

import kotlin.reflect.KClass

actual class Logger actual constructor(loggerName: String) {
    actual fun error(text: Any?, exception: Throwable?) {
//        console(text, exception)
    }

    actual fun info(text: Any?, exception: Throwable?) {
//        console(text, exception)
    }

    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())
}
