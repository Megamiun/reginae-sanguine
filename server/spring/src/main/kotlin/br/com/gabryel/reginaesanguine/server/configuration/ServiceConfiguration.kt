package br.com.gabryel.reginaesanguine.server.configuration

import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun gameService(deckService: DeckService): GameService = GameService(deckService)
}
