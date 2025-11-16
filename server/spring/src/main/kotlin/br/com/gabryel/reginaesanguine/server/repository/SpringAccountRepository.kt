package br.com.gabryel.reginaesanguine.server.repository

import br.com.gabryel.reginaesanguine.server.domain.Account
import br.com.gabryel.reginaesanguine.server.entity.AccountEntity
import br.com.gabryel.reginaesanguine.server.jpa.AccountJpaRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

@Repository
class SpringAccountRepository(
    private val accountJpaRepository: AccountJpaRepository,
) : AccountRepository {
    @Transactional
    override suspend fun save(account: Account): Account = withContext(IO) {
        val entity = AccountEntity.fromDomain(account)
        accountJpaRepository.save(entity).toDomain()
    }

    override suspend fun findByUsername(username: String): Account? = withContext(IO) {
        accountJpaRepository.findByUsername(username)?.toDomain()
    }

    override suspend fun existsByUsername(username: String): Boolean = withContext(IO) {
        accountJpaRepository.existsByUsername(username)
    }

    override suspend fun existsByEmail(email: String): Boolean = withContext(IO) {
        accountJpaRepository.existsByEmail(email)
    }
}
