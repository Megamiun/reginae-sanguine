package br.com.gabryel.reginaesanguine.server.domain.page

import br.com.gabryel.reginaesanguine.server.domain.DeckDto
import kotlinx.serialization.Serializable

/**
 * Concrete implementation of PageDto for PackDto.
 * Serializable for cross-platform compatibility (especially Kotlin/JS).
 */
@Serializable
data class DeckPageDto(
    override val content: List<DeckDto>,
    override val page: Int,
    override val size: Int,
    override val totalElements: Long,
    override val totalPages: Int
) : PageDto<DeckDto>
