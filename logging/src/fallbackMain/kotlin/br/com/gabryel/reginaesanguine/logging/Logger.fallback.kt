package br.com.gabryel.reginaesanguine.logging

import kotlin.reflect.KClass

actual class Logger actual constructor(loggerName: String) {
    actual fun error(text: Any?, exception: Throwable?) {
        println("ERROR: $text: $exception")
        exception?.printStackTrace()
    }

    actual fun info(text: Any?, exception: Throwable?) {
        println("INFO: $text: $exception")
        exception?.printStackTrace()
    }

    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())
}
