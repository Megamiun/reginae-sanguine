package br.com.gabryel.reginaesanguine.app.services

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class ResourcesResourceLoader : ResourceLoader {
    override suspend fun load(path: String): ByteArray = TODO()
}
