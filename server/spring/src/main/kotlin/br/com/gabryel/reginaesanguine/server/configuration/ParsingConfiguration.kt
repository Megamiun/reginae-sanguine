package br.com.gabryel.reginaesanguine.server.configuration

import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class ParsingConfiguration {
    @Bean
    fun json() = gameJsonParser()

    @Bean
    fun messageConverter(json: Json) = KotlinSerializationJsonHttpMessageConverter(json)
}
