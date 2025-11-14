package br.com.gabryel.reginaesanguine.server.dto

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
}
