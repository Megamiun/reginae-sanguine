package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import br.com.gabryel.reginaesanguine.server.test.ServerClient
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.Spec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.testContextManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
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
    override lateinit var client: ServerClient

    override suspend fun beforeSpec(spec: Spec) {
        val context = testContextManager().testContext.applicationContext as WebApplicationContext
        val restClient = RestTestClient
            .bindToApplicationContext(context)
            .configureMessageConverters<WebAppContextSetupBuilder> {
                it.stringMessageConverter(StringHttpMessageConverter())
                it.jsonMessageConverter(KotlinSerializationJsonHttpMessageConverter(json))
            }
            .build()

        client = SpringServerClient(restClient, json)

        super.beforeSpec(spec)
    }
}
