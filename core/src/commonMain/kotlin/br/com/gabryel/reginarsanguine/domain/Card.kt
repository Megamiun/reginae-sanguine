package br.com.gabryel.reginarsanguine.domain

// TODO Temporary, enum will be just for testing
enum class Card(val id: String, val increments: Map<Pair<Int, Int>, Int>, val value: Int, val price: Int) {
    SECURITY_OFFICER("QB01-001", mapOf(
        1 to 0 to 1,
        0 to 1 to 1,
        -1 to 0 to 1,
        0 to -1 to 1
    ), 1, 1),
    RIOT_TROOPER("QB01-002", mapOf(
        1 to 0 to 1,
        2 to 0 to 1,
        0 to 1 to 1,
        -1 to 0 to 1,
        -2 to 0 to 1
    ), 1, 1)
}