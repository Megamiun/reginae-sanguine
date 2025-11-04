package br.com.gabryel.reginaesanguine.server.service

import br.com.gabryel.reginaesanguine.domain.Pack
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service

@Service
class SpringDeckService(private val json: Json) : DeckService() {
    override fun loadPack(packId: String): Pack? {
        val resource = javaClass.getResourceAsStream("/packs/$packId/pack_info.json")
            ?: return null

        return json.decodeFromString<Pack>(resource.bufferedReader().readText())
    }
}
