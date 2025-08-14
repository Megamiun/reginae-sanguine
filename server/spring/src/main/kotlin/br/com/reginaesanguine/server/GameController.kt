package br.com.reginaesanguine.server

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Configuration
class ParsingConfiguration {
    @Bean fun json() = Json { ignoreUnknownKeys = true }

    // If commented: Uses Jackson Message Converter. Ignores kotlinx-serialization
    // If not commented: Uses Kotlinx Serialization Converter. Fails with Response Entity
    @Bean fun messageConverter(json: Json) = KotlinSerializationJsonHttpMessageConverter(json)
}

@Serializable
data class GameSummary(
    val state: String,
    @Transient val test: String = ""
)

@RestController
@RequestMapping("game")
class GameController(private val json: Json) {
    val logger = LoggerFactory.getLogger(GameController::class.java)

    @GetMapping("/response-entity")
    fun createGameResponseEntity(): ResponseEntity<GameSummary> = ResponseEntity.ok(getSummary())

    @GetMapping("/direct")
    fun createGameB(): GameSummary = getSummary()

    private fun getSummary(): GameSummary {
        val body = GameSummary("WON", "[TRANSIENT] SHOULD NOT APPEAR")

        logger.info("Should be: ${json.encodeToString(body)}")
        return body
    }
}
