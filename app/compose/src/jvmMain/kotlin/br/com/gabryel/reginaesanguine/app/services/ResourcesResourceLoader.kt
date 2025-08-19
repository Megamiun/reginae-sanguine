package br.com.gabryel.reginaesanguine.app.services

class ResourcesResourceLoader : ResourceLoader {
    override suspend fun load(path: String): ByteArray = this.javaClass.getResourceAsStream("/$path")
        ?.readBytes()
        ?: throw IllegalStateException("No file found on resource path: $path")
}
