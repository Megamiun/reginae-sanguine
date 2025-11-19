package br.com.gabryel.reginaesanguine.app.service

import br.com.gabryel.reginaesanguine.app.services.ResourceLoader
import br.com.gabryel.reginaesanguine.logging.Logger
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

class WebResourceLoader : ResourceLoader {
    val logger = Logger(this::class)

    override suspend fun load(path: String): ByteArray {
        logger.info("Loading $path")
        return runCatching {
            window.fetch(path).await()
                .arrayBuffer().await()
                .asByteArray()
        }.getOrElse {
            logger.error("Couldn't load $path", it)
            ByteArray(0)
        }
    }
}

fun ArrayBuffer.asByteArray(): ByteArray {
    val bytes = Uint8Array(this)

    return (0 until byteLength)
        .map { bytes[it] }.toByteArray()
}
