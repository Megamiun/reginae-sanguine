package br.com.gabryel.reginaesanguine.viewmodel

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun <reified T> require(item: Any) {
    contract {
        returns() implies (item is T)
    }

    require(item is T) { "${item::class.simpleName} is not ${T::class.simpleName}" }
}
