package br.com.gabryel.reginaesanguine.server.domain

import kotlinx.serialization.Serializable

/**
 * Concrete implementation of PageDto for PackDto.
 * Serializable for cross-platform compatibility (especially Kotlin/JS).
 */
@Serializable
data class PackPageDto(
    override val content: List<PackDto>,
    override val page: Int,
    override val size: Int,
    override val totalElements: Long,
    override val totalPages: Int
) : PageDto<PackDto>
