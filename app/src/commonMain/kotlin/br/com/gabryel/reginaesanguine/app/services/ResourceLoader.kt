package br.com.gabryel.reginaesanguine.app.services

interface ResourceLoader {
    fun load(path: String): ByteArray
}
