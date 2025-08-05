package br.com.gabryel.reginaesanguine.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TODO Consider separating into id and Card types?
@Serializable
sealed interface Action<CARD_TYPE> {
    @Serializable
    @SerialName("Skip")
    data object Skip : Action<Nothing>

    @Serializable
    @SerialName("Play")
    data class Play<CARD_TYPE>(val position: Position, val card: CARD_TYPE) : Action<CARD_TYPE>
}
