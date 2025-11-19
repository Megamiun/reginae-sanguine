package br.com.gabryel.reginaesanguine.logging

import java.util.logging.Level.INFO
import java.util.logging.Level.SEVERE
import java.util.logging.Logger
import kotlin.reflect.KClass

actual class Logger actual constructor(loggerName: String) {
    actual constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())

    private val logger = Logger.getLogger(loggerName)

    actual fun error(text: Any?, exception: Throwable?) {
        logger.log(SEVERE, "$text", exception)
    }

    actual fun info(text: Any?, exception: Throwable?) {
        logger.log(INFO, "$text", exception)
    }
}
