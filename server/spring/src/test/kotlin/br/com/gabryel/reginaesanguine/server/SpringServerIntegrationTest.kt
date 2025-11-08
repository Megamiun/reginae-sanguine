package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import br.com.gabryel.reginaesanguine.server.service.SeedResult
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.Spec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.testContextManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.RestTestClient.WebAppContextSetupBuilder
import org.springframework.web.context.WebApplicationContext

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ApplyExtension(TestContainersExtension::class, SpringExtension::class)
class SpringServerIntegrationTest : AbstractServerIntegrationTest() {
    private val json = gameJsonParser()

    private lateinit var client: RestTestClient

    @Autowired
    private lateinit var packSeederService: PackSeederService

    override suspend fun beforeSpec(spec: Spec) {
        val context = testContextManager().testContext.applicationContext as WebApplicationContext
        client = RestTestClient
            .bindToApplicationContext(context)
            .configureMessageConverters<WebAppContextSetupBuilder> {
                it.stringMessageConverter(StringHttpMessageConverter())
                it.jsonMessageConverter(KotlinSerializationJsonHttpMessageConverter(json))
            }
            .build()

        super.beforeSpec(spec)
    }

    override suspend fun postInitGame(request: InitGameRequest, playerPosition: PlayerPosition): GameIdDto {
        val response = client.post()
            .uri("/game")
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header("Authorization", playerPosition.name)
            .body(request)
            .exchange()
            .returnResult(String::class.java)

        val body = response.responseBody ?: throw IllegalStateException("Empty response")

        try {
            return json.decodeFromString(body)
        } catch (e: Exception) {
            throw IllegalStateException("Failed to parse response. Body: $body", e)
        }
    }

    override suspend fun getGameStatus(gameId: String, playerPosition: PlayerPosition): GameViewDto =
        client.get()
            .uri("/game/$gameId/status")
            .header("Authorization", playerPosition.name)
            .exchange()
            .returnResult(GameViewDto::class.java)
            .responseBody
            ?: throw IllegalStateException("Empty response")

    override suspend fun postAction(gameId: String, playerPosition: PlayerPosition, action: ActionDto): GameViewDto =
        client.post()
            .uri("/game/$gameId/action")
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header("Authorization", playerPosition.name)
            // TODO Sending as a String for now. Should raise issue on Spring Web
            .body(json.encodeToString(action))
            .exchange()
            .returnResult(GameViewDto::class.java)
            .responseBody
            ?: throw IllegalStateException("Empty response")

    override suspend fun seedPacks(): SeedResult = packSeederService.seedPacks()
}
