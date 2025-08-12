package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.Displacement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface Destroy : EffectWithAffected

@Serializable
@SerialName("DestroyCards")
class DestroyCards(
    override val target: TargetType,
    override val trigger: Trigger,
    override val affected: Set<Displacement> = setOf(),
    override val description: String = "Destroy $target cards on $trigger",
) : Destroy
