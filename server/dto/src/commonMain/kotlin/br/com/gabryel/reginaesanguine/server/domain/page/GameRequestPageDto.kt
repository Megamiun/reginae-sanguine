package br.com.gabryel.reginaesanguine.server.domain.page

import br.com.gabryel.reginaesanguine.server.domain.GameRequestDto
import kotlinx.serialization.Serializable

/**
 * Concrete implementation of PageDto for GameRequestDto.
 * Serializable for cross-platform compatibility (especially Kotlin/JS).
 */
@Serializable
data class GameRequestPageDto(
    override val content: List<GameRequestDto>,
    override val page: Int,
    override val size: Int,
    override val totalElements: Long,
    override val totalPages: Int
) : PageDto<GameRequestDto>
