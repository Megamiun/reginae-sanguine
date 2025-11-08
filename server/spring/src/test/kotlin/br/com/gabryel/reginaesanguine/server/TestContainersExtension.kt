package br.com.gabryel.reginaesanguine.server

import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.reflect.KClass

class TestContainersExtension : PrepareSpecListener {
    private lateinit var postgreSQLContainer: PostgreSQLContainer<*>

    override suspend fun prepareSpec(kclass: KClass<out Spec>) {
        postgreSQLContainer = PostgreSQLContainer("postgres:17.0-alpine")
            .withDatabaseName("reginae_sanguine")
            .withUsername("postgres")
            .withPassword("postgres")
            .withReuse(false)

        postgreSQLContainer.start()

        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl())
        System.setProperty("spring.flyway.url", postgreSQLContainer.getJdbcUrl())
    }
}
