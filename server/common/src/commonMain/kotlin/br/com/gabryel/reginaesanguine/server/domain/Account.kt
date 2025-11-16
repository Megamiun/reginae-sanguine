package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.datetime.LocalDateTime

data class Account(
    val id: String,
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
