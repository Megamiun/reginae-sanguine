package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.server.node.StartedPostgreSqlContainer
import br.com.gabryel.reginaesanguine.server.node.createPostgreSqlContainer
import io.kotest.common.KotestInternal
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.log
import io.kotest.core.spec.Spec
import kotlinx.coroutines.await
import kotlin.reflect.KClass

/**
 * Kotest extension that manages a PostgreSQL TestContainer for Node.js tests.
 * Similar to the Spring TestContainersExtension but for Kotlin/JS.
 */
@OptIn(KotestInternal::class)
class NodeTestContainersExtension : PrepareSpecListener, AfterSpecListener {
    private var container: StartedPostgreSqlContainer? = null

    override suspend fun prepareSpec(kclass: KClass<out Spec>) {
        log { "Starting PostgreSQL TestContainer..." }

        val postgreSqlContainer = createPostgreSqlContainer()
        container = postgreSqlContainer.start().await()
    }

    override suspend fun afterSpec(spec: Spec) {
        log { "Stopping PostgreSQL TestContainer..." }

        container?.stop()?.await()
        container = null

        log { "PostgreSQL TestContainer stopped" }
    }

    fun getConnectionConfig(): DatabaseConfig {
        val psqlContainer = requireNotNull(container) { "Container not started" }
        return DatabaseConfig(
            host = psqlContainer.getHost(),
            port = psqlContainer.getPort(),
            database = psqlContainer.getDatabase(),
            user = psqlContainer.getUsername(),
            password = psqlContainer.getPassword(),
        )
    }
}

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String
)
