package br.com.gabryel.reginaesanguine.domain

// Not sure if this is the most interesting impl
typealias Position = Pair<Int, Int>
typealias Displacement = Pair<Int, Int>
typealias Size = Pair<Int, Int>

fun Pair<Int, Int>.lane() = first

fun Pair<Int, Int>.column() = second

operator fun Position.plus(displacement: Displacement): Position = lane() + displacement.lane() to column() + displacement.column()

fun Position.constrainTo(size: Size): Position = lane().mod(size.lane()) to column().mod(size.column())
