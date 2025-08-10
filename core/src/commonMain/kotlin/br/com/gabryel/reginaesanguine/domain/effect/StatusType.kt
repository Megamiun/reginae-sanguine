package br.com.gabryel.reginaesanguine.domain.effect

enum class StatusType {
    ENHANCED,
    ENFEEBLED,
    ANY;

    fun isUnderStatus(points: Int) = when (this) {
        ENHANCED -> points > 0
        ENFEEBLED -> points < 0
        ANY -> points != 0
    }
}
