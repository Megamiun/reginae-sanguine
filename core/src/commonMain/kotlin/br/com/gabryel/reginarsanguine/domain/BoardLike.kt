package br.com.gabryel.reginarsanguine.domain

interface BoardLike {
    fun at(row: Int, column: Int): Cell
}