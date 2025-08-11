package br.com.gabryel.reginaesanguine.domain.effect

import br.com.gabryel.reginaesanguine.domain.PlayerPosition

enum class TargetType {
    ALLIES,
    ENEMIES,
    ANY,
    SELF,
    NONE;

    fun isTargetable(source: PlayerPosition, target: PlayerPosition, self: Boolean) = when (this) {
        ALLIES -> source == target && !self
        ENEMIES -> source != target
        ANY -> !self
        SELF -> self
        NONE -> false
    }
}
