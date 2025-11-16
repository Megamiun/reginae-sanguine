package br.com.gabryel.reginaesanguine.server.node.repository

import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import br.com.gabryel.reginaesanguine.server.node.pg.PoolClient
import br.com.gabryel.reginaesanguine.server.node.require
import kotlinx.coroutines.await

private val crypto = require("crypto")

// TODO Exchange to another better impl
fun generateUUID(): String = crypto.randomUUID()

/**
 * Execute a block of code within a PostgreSQL transaction.
 * Automatically commits on success and rolls back on failure.
 */
suspend fun <T> withTransaction(pool: Pool, block: suspend (PoolClient) -> T): T {
    val client = pool.connect().await()
    return try {
        client.query("BEGIN").await()
        val result = block(client)
        client.query("COMMIT").await()
        result
    } catch (e: Throwable) {
        try {
            client.query("ROLLBACK").await()
        } catch (rollbackError: Throwable) {
            console.error("Error during rollback: ${rollbackError.message}")
        }
        throw e
    } finally {
        client.release()
    }
}
