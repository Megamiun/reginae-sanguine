package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.domain.PlayerPosition
import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.configuration.UUIDSerializer
import br.com.gabryel.reginaesanguine.server.domain.ActionDto
import br.com.gabryel.reginaesanguine.server.domain.GameIdDto
import br.com.gabryel.reginaesanguine.server.domain.GameViewDto
import br.com.gabryel.reginaesanguine.server.domain.action.InitGameRequest
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.Spec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.testContextManager
import kotlinx.serialization.modules.contextual
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
@ApplyExtension(SpringExtension::class)
class SpringServerIntegrationTest : AbstractServerIntegrationTest() {
    private val json = gameJsonParser { contextual(UUIDSerializer) }

    private lateinit var client: RestTestClient

    override suspend fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)

        val context = testContextManager().testContext.applicationContext as WebApplicationContext
        client = RestTestClient
            .bindToApplicationContext(context)
            .configureMessageConverters<WebAppContextSetupBuilder> {
                it.stringMessageConverter(StringHttpMessageConverter())
                it.jsonMessageConverter(KotlinSerializationJsonHttpMessageConverter(json))
            }
            .build()
    }

    override suspend fun postInitGame(request: InitGameRequest, playerPosition: PlayerPosition): GameIdDto =
        client.post()
            .uri("/game")
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .header("Authorization", playerPosition.name)
            .body(request)
            .exchange()
            .returnResult(GameIdDto::class.java)
            .responseBody
            ?: throw IllegalStateException("Empty response")

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
}
