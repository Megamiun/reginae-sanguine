package br.com.gabryel.reginaesanguine.server.jpa

import br.com.gabryel.reginaesanguine.server.entity.PackCardEffectEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PackCardEffectJpaRepository : JpaRepository<PackCardEffectEntity, UUID>
