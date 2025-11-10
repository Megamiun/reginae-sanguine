package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.server.test.ServerClient
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.client.RestTestClient
import org.springframework.test.web.servlet.client.RestTestClient.RequestBodySpec
import org.springframework.test.web.servlet.client.RestTestClient.RequestHeadersSpec
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.reflect.KClass

class SpringServerClient(private val client: RestTestClient, private val json: Json) : ServerClient {
    override suspend fun <T : Any> get(path: String, responseClass: KClass<T>, headers: Map<String, String>): T =
        client.get()
            .uri(path)
            .withHeaders(headers)
            .exchange()
            .returnResult(responseClass.java)
            .responseBody
            ?: throw IllegalStateException("Empty response")

    override suspend fun <T : Any, V : Any> post(
        path: String,
        body: T?,
        requestClass: KClass<T>,
        responseClass: KClass<V>,
        headers: Map<String, String>
    ): V =
        client.post()
            .uri(path)
            .contentType(APPLICATION_JSON)
            .withHeaders(headers)
            .withBody(body, requestClass)
            .exchange()
            .returnResult(responseClass.java)
            .responseBody
            ?: throw IllegalStateException("Empty response")

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> RequestBodySpec.withBody(body: T?, requestClass: KClass<T>): RequestHeadersSpec<*> =
        if (body == null)
            this
        else
            body(json.encodeToString(requestClass.serializer(), body))

    @OptIn(InternalSerializationApi::class)
    private fun <T : RequestHeadersSpec<out T>> T.withHeaders(headers: Map<String, String>): T =
        headers { requestHeaders -> headers.forEach { (key, value) -> requestHeaders.add(key, value) } }
}
