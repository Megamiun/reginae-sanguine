package br.com.gabryel.reginaesanguine.app.config

object ServerConfig {
    val DEFAULT_PROFILES = listOf(
        ServerProfile("Docker Spring", "http://rs-spring:8080"),
        ServerProfile("Docker Node", "http://rs-node:3000"),
    )
}

data class ServerProfile(
    val name: String,
    val url: String,
)
