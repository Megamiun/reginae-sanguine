package br.com.gabryel.reginaesanguine.server.node

import br.com.gabryel.reginaesanguine.server.node.pg.Pool
import kotlinx.coroutines.await

/**
 * Migration runner for Node.js that loads SQL migrations from disk.
 * Tracks applied migrations in a version table similar to Flyway.
 */
object MigrationRunner {
    private val fs = require("fs")

    data class Migration(
        val version: Int,
        val description: String,
        val filename: String,
        val sql: String,
    )

    /**
     * Runs all pending migrations in order.
     * Creates version table and tracks applied migrations.
     */
    suspend fun runMigrations(pool: Pool) {
        createVersionTable(pool)

        val migrations = loadMigrations()
        val appliedVersions = getAppliedVersions(pool)

        migrations
            .filter { it.version !in appliedVersions }
            .sortedBy { it.version }
            .forEach { migration -> executeMigration(pool, migration) }
    }

    private suspend fun createVersionTable(pool: Pool) {
        pool.query(
            """
            CREATE TABLE IF NOT EXISTS flyway_schema_history (
                installed_rank SERIAL PRIMARY KEY,
                version VARCHAR(50) NOT NULL,
                description VARCHAR(200) NOT NULL,
                script VARCHAR(1000) NOT NULL,
                checksum INTEGER,
                installed_by VARCHAR(100) NOT NULL DEFAULT 'node',
                installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                execution_time INTEGER NOT NULL,
                success BOOLEAN NOT NULL,
                UNIQUE(version)
            );
            """.trimIndent(),
        ).await()
    }

    private suspend fun getAppliedVersions(pool: Pool): Set<Int> {
        val result = pool.query("SELECT version FROM flyway_schema_history WHERE success = true").await()
        return result.rows
            .map { (it.version as String).toInt() }
            .toSet()
    }

    private fun loadMigrations(): List<Migration> {
        val migrationDir = "kotlin/db/migration"

        if (!fileExistsSync(migrationDir)) {
            console.warn("Migration directory not found: $migrationDir")
            return emptyList()
        }

        val files = readdirSync(migrationDir) as Array<String>

        return files
            .filter { it.startsWith("V") && it.endsWith(".sql") }
            .mapNotNull { filename ->
                try {
                    parseMigration(migrationDir, filename)
                } catch (e: Throwable) {
                    console.error("Error parsing migration $filename: ${e.message}")
                    null
                }
            }
    }

    private fun parseMigration(dir: String, filename: String): Migration {
        val versionPart = filename.substringAfter("V").substringBefore("__")
        val descriptionPart = filename
            .substringAfter("__")
            .substringBefore(".sql")
            .replace("_", " ")

        val version = versionPart.toInt()
        val sql = readFileSync("$dir/$filename")

        return Migration(version, descriptionPart, filename, sql)
    }

    private suspend fun executeMigration(pool: Pool, migration: Migration) {
        val startTime = js("Date.now()") as Int

        try {
            pool.query(migration.sql).await()

            val endTime = js("Date.now()") as Int
            val executionTime = endTime - startTime

            val insertSql =
                """
                INSERT INTO flyway_schema_history (version, description, script, execution_time, success)
                VALUES ($1, $2, $3, $4, $5)
                """.trimIndent()

            pool.query(
                insertSql,
                arrayOf(migration.version.toString(), migration.description, migration.filename, executionTime, true),
            ).await()

            console.log("Migration ${migration.filename} executed successfully in ${executionTime}ms")
        } catch (e: Throwable) {
            console.error("Error running migration ${migration.filename}: ${e.message}", e)

            val insertSql =
                """
                INSERT INTO flyway_schema_history (version, description, script, execution_time, success)
                VALUES ($1, $2, $3, $4, $5)
                """.trimIndent()

            pool.query(
                insertSql,
                arrayOf(migration.version.toString(), migration.description, migration.filename, 0, false),
            ).await()

            throw e
        }
    }

    private fun fileExistsSync(path: String): Boolean =
        try {
            fs.existsSync(path) as Boolean
        } catch (e: Throwable) {
            console.log("Error checking for file existence", e)
            false
        }

    private fun readFileSync(path: String): String = fs.readFileSync(path, "utf8") as String

    private fun readdirSync(path: String): dynamic = fs.readdirSync(path)
}
