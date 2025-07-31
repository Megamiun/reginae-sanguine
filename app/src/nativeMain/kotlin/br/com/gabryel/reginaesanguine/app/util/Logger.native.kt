package br.com.gabryel.reginaesanguine.app.util

import kotlin.reflect.KClass

actual class Logger actual constructor(loggerName: String) {
    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())

    actual fun error(text: Any?, exception: Throwable?) {
        TODO("Not yet implemented")
    }

    actual fun info(text: Any?, exception: Throwable?) {
        TODO("Not yet implemented")
    }
}
