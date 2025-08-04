package br.com.gabryel.reginaesanguine.app.util

import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

class Logger(loggerName: String) {
    constructor(clazz: KClass<*>) : this(clazz.simpleName.orEmpty())

    private val logger = Logger.getLogger(loggerName)

    fun error(text: Any?, exception: Throwable?) {
        logger.log(Level.SEVERE, "$text", exception)
    }

    fun info(text: Any?, exception: Throwable?) {
        logger.log(Level.INFO, "$text", exception)
    }
}
