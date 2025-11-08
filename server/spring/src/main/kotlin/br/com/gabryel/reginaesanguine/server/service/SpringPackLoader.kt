package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component

@Component
class SpringPackLoader : PackLoader {
    private val json = gameJsonParser()
    private val resourceResolver = PathMatchingResourcePatternResolver()

    override fun loadAllPacks(): List<Pack> =
        resourceResolver.getResources("classpath:packs/*/pack_info.json").mapNotNull { resource ->
            try {
                val jsonContent = resource.inputStream.readBytes().decodeToString()
                json.decodeFromString<Pack>(jsonContent)
            } catch (e: Exception) {
                println("Failed to load pack from ${resource.filename}: ${e.message}")
                throw e
            }
        }
}
