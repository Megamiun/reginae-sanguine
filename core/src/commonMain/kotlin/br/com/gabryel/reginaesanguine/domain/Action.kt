package br.com.gabryel.reginaesanguine.domain

// TODO Consider separating into id and Card types?
sealed interface Action<CARD_TYPE> {
    data object Skip : Action<Nothing>

    data class Play<CARD_TYPE>(val position: Position, val card: CARD_TYPE) : Action<CARD_TYPE>
}
