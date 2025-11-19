package br.com.gabryel.reginaesanguine.server.client

import io.ktor.client.plugins.logging.Logger
import kotlin.reflect.KClass

private typealias RSLogger = br.com.gabryel.reginaesanguine.logging.Logger

class KtorLogger(clazz: KClass<*>) : Logger {
    private val logger = RSLogger(clazz)

    override fun log(message: String) {
        logger.info(message)
    }
}
