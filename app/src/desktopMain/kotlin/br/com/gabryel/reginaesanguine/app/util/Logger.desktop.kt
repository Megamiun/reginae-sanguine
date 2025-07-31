package br.com.gabryel.reginaesanguine.app.util

import java.util.logging.Level.SEVERE
import java.util.logging.Logger
import kotlin.reflect.KClass

actual class Logger actual constructor(val clazz: KClass<*>) {
    private val logger = Logger.getLogger(clazz.simpleName)

    actual fun error(text: String) {
        logger.log(SEVERE, text)
    }

    actual fun error(text: String, exception: Throwable) {
        logger.log(SEVERE, text, exception)
    }
}
