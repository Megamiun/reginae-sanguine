package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.server.client.KtorServerClient
import br.com.gabryel.reginaesanguine.server.client.ServerClient
import br.com.gabryel.reginaesanguine.server.node.runServer
import br.com.gabryel.reginaesanguine.server.test.AbstractServerIntegrationTest
import io.kotest.common.KotestInternal
import io.kotest.core.log
import io.kotest.core.spec.Spec

/**
 * Node.js integration test implementation using Kotest FunSpec.
 * Embeds the Express server in-process for testing with TestContainers.
 *
 * Run with: ./gradlew :server:node:jsTest
 */
@OptIn(KotestInternal::class)
class NodeServerIntegrationTest : AbstractServerIntegrationTest() {
    private val testContainersExtension = NodeTestContainersExtension()

    private var server: dynamic = null
    private val testPort = 3001

    override var client: ServerClient = KtorServerClient("http://localhost:$testPort")

    override suspend fun beforeSpec(spec: Spec) {
        testContainersExtension.prepareSpec(NodeServerIntegrationTest::class)

        val config = testContainersExtension.getConnectionConfig()

        server = runServer(
            dbHost = config.host,
            dbPort = config.port,
            dbName = config.database,
            dbUser = config.user,
            dbPassword = config.password,
            jwtPrivateKey = loadTestPrivateKey(),
            jwtPublicKey = loadTestPublicKey(),
            port = testPort,
        )

        server?.unref()
        super.beforeSpec(spec)
    }

    private fun loadTestPrivateKey(): String = loadResource("jwt/private.pem")

    private fun loadTestPublicKey(): String = loadResource("jwt/public.pem")

    private fun loadResource(path: String): String {
        val fs = js("require('fs')")
        val pathModule = js("require('path')")
        val resourcePath = pathModule.join(js("__dirname"), path) as String
        return fs.readFileSync(resourcePath, "utf-8") as String
    }

    override suspend fun afterSpec(spec: Spec) {
        log { "Closing server..." }
        server?.close()
        server = null
        log { "Server closed" }

        testContainersExtension.afterSpec(spec)
        super.afterSpec(spec)
    }
}
