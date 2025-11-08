package br.com.gabryel.reginaesanguine.server.configuration

import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService
import br.com.gabryel.reginaesanguine.server.service.PackLoader
import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun deckService(packRepository: PackRepository): DeckService = DeckService(packRepository)

    @Bean
    fun gameService(deckService: DeckService): GameService = GameService(deckService)

    @Bean
    fun packSeederService(
        packRepository: PackRepository,
        packLoader: PackLoader
    ): PackSeederService = PackSeederService(packRepository, packLoader)
}
