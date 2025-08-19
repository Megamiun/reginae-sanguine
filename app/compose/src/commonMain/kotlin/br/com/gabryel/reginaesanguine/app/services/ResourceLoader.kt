package br.com.gabryel.reginaesanguine.app.services

interface ResourceLoader {
    suspend fun load(path: String): ByteArray
}
