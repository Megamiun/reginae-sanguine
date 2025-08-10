package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition

enum class TargetType {
    ALLIES,
    ENEMIES,
    ANY,
    SELF,
    NONE;

    fun isTargetable(source: PlayerPosition, target: PlayerPosition) = when (this) {
        ALLIES -> source == target
        ENEMIES -> source != target
        ANY -> true
        SELF -> TODO("SELF not implemented yet")
        NONE -> false
    }
}
