package br.com.gabryel.reginaesanguine.server.test

import kotlin.reflect.KClass

interface ServerClient {
    suspend fun <T : Any> get(
        path: String,
        responseClass: KClass<T>,
        headers: Map<String, String> = emptyMap()
    ): T

    suspend fun <T : Any, V : Any> post(
        path: String,
        body: T? = null,
        requestClass: KClass<T>,
        responseClass: KClass<V>,
        headers: Map<String, String> = emptyMap()
    ): V
}

suspend inline fun <reified T : Any> ServerClient.get(path: String, headers: Map<String, String> = emptyMap()) =
    get(path, T::class, headers)

suspend inline fun <reified T : Any, reified V : Any> ServerClient.post(
    path: String,
    body: T? = null,
    headers: Map<String, String> = emptyMap()
) = post(path, body, T::class, V::class, headers)
