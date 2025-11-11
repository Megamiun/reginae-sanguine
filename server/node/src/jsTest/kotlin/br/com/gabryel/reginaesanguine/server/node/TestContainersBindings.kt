package br.com.gabryel.reginaesanguine.server.node

import kotlin.js.Promise

/**
 * External bindings for the @testcontainers/postgresql npm package.
 * See: https://node.testcontainers.org/
 */
external interface PostgreSqlContainerType {
    fun start(): Promise<StartedPostgreSqlContainer>
}

external interface StartedPostgreSqlContainer {
    fun stop(): Promise<Unit>

    fun getHost(): String

    fun getPort(): Int

    fun getDatabase(): String

    fun getUsername(): String

    fun getPassword(): String
}

/**
 * Creates a PostgreSQL container using testcontainers.
 */
fun createPostgreSqlContainer(): PostgreSqlContainerType {
    val module = require("@testcontainers/postgresql")
    val postgreSqlContainer = module.PostgreSqlContainer
    val container = js("new postgreSqlContainer('postgres:17.0-alpine')")
    return container.unsafeCast<PostgreSqlContainerType>()
}
