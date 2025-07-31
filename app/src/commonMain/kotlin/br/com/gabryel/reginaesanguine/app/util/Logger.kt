package br.com.gabryel.reginaesanguine.app.util

import kotlin.reflect.KClass

expect class Logger(loggerName: String) {
    constructor(clazz: KClass<*>)

    fun error(text: Any?, exception: Throwable? = null)

    fun info(text: Any?, exception: Throwable? = null)
}
