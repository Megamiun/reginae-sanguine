package br.com.gabryel.reginaesanguine.app.util

import kotlin.reflect.KClass

expect class Logger(clazz: KClass<*>) {
    fun error(text: String)

    fun error(text: String, exception: Throwable)
}
