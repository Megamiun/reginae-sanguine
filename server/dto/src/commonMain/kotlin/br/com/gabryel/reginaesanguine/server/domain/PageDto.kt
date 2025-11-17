package br.com.gabryel.reginaesanguine.server.domain

/**
 * Generic pagination interface.
 * Concrete implementations should be @Serializable for platform-specific serialization.
 */
interface PageDto<T> {
    val content: List<T>
    val page: Int
    val size: Int
    val totalElements: Long
    val totalPages: Int

    fun <V : PageDto<T>> upcast(upcaster: (List<T>, Int, Int, Long, Int) -> V): V =
        upcaster(content, page, size, totalElements, totalPages)

    companion object {
        fun <T> singlePage(content: List<T>): PageDto<T> =
            GenericPageDto(content, 0, content.size, content.size.toLong(), 1)
    }
}

inline fun <T, V> PageDto<T>.map(mapper: (T) -> V): PageDto<V> =
    GenericPageDto(content.map(mapper), page, size, totalElements, totalPages)
