package br.com.gabryel.reginaesanguine.server.configuration

import br.com.gabryel.reginaesanguine.server.repository.AccountRepository
import br.com.gabryel.reginaesanguine.server.repository.PackRepository
import br.com.gabryel.reginaesanguine.server.service.AccountService
import br.com.gabryel.reginaesanguine.server.service.DeckService
import br.com.gabryel.reginaesanguine.server.service.GameService
import br.com.gabryel.reginaesanguine.server.service.PackLoader
import br.com.gabryel.reginaesanguine.server.service.PackSeederService
import br.com.gabryel.reginaesanguine.server.service.security.PasswordHasher
import br.com.gabryel.reginaesanguine.server.service.security.TokenService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun deckService(packRepository: PackRepository) = DeckService(packRepository)

    @Bean
    fun gameService(deckService: DeckService) = GameService(deckService)

    @Bean
    fun packSeederService(
        packRepository: PackRepository,
        packLoader: PackLoader
    ) = PackSeederService(packRepository, packLoader)

    @Bean
    fun accountService(
        accountRepository: AccountRepository,
        passwordHasher: PasswordHasher,
        tokenService: TokenService,
    ) = AccountService(accountRepository, passwordHasher, tokenService)

    @Bean
    fun jwtProperties(
        @Value($$"${app.jwt.private-key}") privateKey: String?,
        @Value($$"${app.jwt.public-key}") publicKey: String?
    ): JwtProperties {
        val privateKey = privateKey?.takeIf { it.isNotBlank() }
            ?: ServiceConfiguration::class.java.getResource("/jwt/private.pem")?.readText()
        val publicKey = publicKey?.takeIf { it.isNotBlank() }
            ?: ServiceConfiguration::class.java.getResource("/jwt/public.pem")?.readText()

        return JwtProperties(requireNotNull(privateKey), requireNotNull(publicKey))
    }
}
