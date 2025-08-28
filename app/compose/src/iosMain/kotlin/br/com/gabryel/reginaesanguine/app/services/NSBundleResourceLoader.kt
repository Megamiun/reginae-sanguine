package br.com.gabryel.reginaesanguine.app.services

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfFile
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class NSBundleResourceLoader : ResourceLoader {
    override suspend fun load(path: String): ByteArray {
        val resourcePath = NSBundle.mainBundle.pathForResource(
            name = path.substringBeforeLast("."),
            ofType = path.substringAfterLast(".")
        ) ?: error("Resource not found: $path")

        val data = NSData.dataWithContentsOfFile(resourcePath)
            ?: error("Could not load resource: $path")

        return ByteArray(data.length.toInt()).apply {
            usePinned { memcpy(it.addressOf(0), data.bytes, data.length) }
        }
    }
}
