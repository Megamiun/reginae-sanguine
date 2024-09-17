package br.com.gabryel.reginarsanguine.domain

// Not sure if this is the most interesting impl
typealias Position = Pair<Int, Int>
typealias Displacement = Pair<Int, Int>

fun Pair<Int, Int>.row() = first
fun Pair<Int, Int>.column() = second

operator fun Position.plus(displacement: Displacement): Position =
    row() + displacement.row() to column() + displacement.column()
