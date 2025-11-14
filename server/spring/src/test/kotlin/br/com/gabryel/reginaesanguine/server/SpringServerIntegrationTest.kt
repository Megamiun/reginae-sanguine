package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.server.client.KtorServerClient
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.Spec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ApplyExtension(TestContainersExtension::class, SpringExtension::class)
class SpringServerIntegrationTest : AbstractServerIntegrationTest() {
    @LocalServerPort
    private var port: Int = 0

    override lateinit var client: ServerClient

    override suspend fun beforeSpec(spec: Spec) {
        client = KtorServerClient("http://localhost:$port")
        super.beforeSpec(spec)
    }
}
