package br.com.gabryel.reginaesanguine.server.domain.page

data class GenericPageDto<T>(
    override val content: List<T>,
    override val page: Int,
    override val size: Int,
    override val totalElements: Long,
    override val totalPages: Int
) : PageDto<T>
