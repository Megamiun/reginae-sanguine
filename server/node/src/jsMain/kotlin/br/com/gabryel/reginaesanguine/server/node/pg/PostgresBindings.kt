package br.com.gabryel.reginaesanguine.server.node.pg

import br.com.gabryel.reginaesanguine.server.node.require
import kotlin.js.Promise

/**
 * PostgreSQL connection pool.
 * This is a dynamic wrapper around the pg.Pool class.
 */
external interface Pool {
    fun query(sql: String, values: Array<Any>? = definedExternally): Promise<QueryResult>

    fun query(sql: String): Promise<QueryResult>

    fun connect(): Promise<PoolClient>

    fun end(): Promise<Unit>
}

/**
 * PostgreSQL client from pool connection.
 */
external interface PoolClient {
    fun query(sql: String, values: Array<Any>? = definedExternally): Promise<QueryResult>

    fun query(sql: String): Promise<QueryResult>

    fun release()
}

external interface QueryResult {
    val rows: Array<dynamic>
    val rowCount: Int
    val command: String
}

fun createPool(
    host: String = "localhost",
    port: Int = 5432,
    database: String = "reginae_sanguine",
    user: String = "postgres",
    password: String = "postgres",
    max: Int = 10
): Pool {
    val pg = require("pg")
    val config = js("{}")
    config.host = host
    config.port = port
    config.database = database
    config.user = user
    config.password = password
    config.max = max

    return js("new pg.Pool(config)").unsafeCast<Pool>()
}
