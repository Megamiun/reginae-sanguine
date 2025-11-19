package br.com.gabryel.reginaesanguine.server.client

import br.com.gabryel.reginaesanguine.domain.parser.gameJsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class KtorServerClient(
    private val baseUrl: String,
    private val json: Json = gameJsonParser()
) : ServerClient {
    private val client = HttpClient {
        install(Logging) {
            logger = KtorLogger(KtorServerClient::class)
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> get(
        path: String,
        responseClass: KClass<T>,
        headers: Map<String, String>
    ): T = client.get(getUrl(path)) { buildRequest(headers, null, Any::class) }
        .body(TypeInfo(responseClass))

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, V : Any> post(
        path: String,
        body: T?,
        requestClass: KClass<T>,
        responseClass: KClass<V>,
        headers: Map<String, String>
    ): V = client.post(getUrl(path)) { buildRequest(headers, body, requestClass) }
        .body(TypeInfo(responseClass))

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, V : Any> put(
        path: String,
        body: T,
        requestClass: KClass<T>,
        responseClass: KClass<V>,
        headers: Map<String, String>
    ): V = client.put(getUrl(path)) { buildRequest(headers, body, requestClass) }
        .body(TypeInfo(responseClass))

    private fun <T : Any> HttpRequestBuilder.buildRequest(
        headers: Map<String, String>,
        body: T?,
        requestClass: KClass<T>
    ) {
        headers {
            headers.forEach { (key, value) ->
                append(key, value)
            }
        }
        if (body != null) {
            contentType(ContentType.Application.Json)
            setBody(body, TypeInfo(requestClass))
        }
    }

    private fun getUrl(path: String): String =
        baseUrl.removeSuffix("/") + "/" + path.removePrefix("/")

    fun close() {
        client.close()
    }
}
