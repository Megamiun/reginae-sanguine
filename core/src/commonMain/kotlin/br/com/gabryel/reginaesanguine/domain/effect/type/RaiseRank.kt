package br.com.gabryel.reginaesanguine.domain.effect.type

import br.com.gabryel.reginaesanguine.domain.effect.None
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * This file contains all effects that implement the RaiseRank interface.
 * RaiseRank effects enhance .
 */
interface RaiseRank : Effect

@Serializable
@SerialName("RaiseRank")
class RaiseRankDefault(
    val amount: Int,
    override val description: String = "Raises rank by $amount"
) : RaiseRank {
    @Transient
    override val trigger = None

    override val discriminator: String = "RaiseRank"
}
