package br.com.gabryel.reginaesanguine.server.configuration

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Server

fun main(vararg args: String) {
    runApplication<Server>(*args)
}
