package br.com.gabryel.reginaesanguine.server

import br.com.gabryel.reginaesanguine.server.test.ServerClient
import kotlinx.coroutines.await
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.js.Promise
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
class NodeServerClient(private val baseUrl: String, private val json: Json) : ServerClient {
    override suspend fun <T : Any> get(path: String, responseClass: KClass<T>, headers: Map<String, String>): T {
        val options = js("{}")
        options.method = "GET"

        val jsHeaders = js("{}")
        jsHeaders["Content-Type"] = "application/json"
        headers.forEach { (key, value) -> jsHeaders[key] = value }
        options.headers = jsHeaders

        val url = baseUrl.removeSuffix("/") + "/" + path.removePrefix("/")
        val response = fetch(url, options).await()
        val body = (response.json() as Promise<dynamic>).await()

        val bodyContent = JSON.stringify(body)

        require(response.ok) { "Failed to execute action: ${response.status} - $bodyContent" }

        return json.decodeFromString(responseClass.serializer(), bodyContent)
    }

    override suspend fun <T : Any, V : Any> post(
        path: String,
        body: T?,
        requestClass: KClass<T>,
        responseClass: KClass<V>,
        headers: Map<String, String>
    ): V {
        val options = js("{}")
        options.method = "POST"

        // Convert headers map to JavaScript object
        val jsHeaders = js("{}")
        jsHeaders["Content-Type"] = "application/json"
        headers.forEach { (key, value) -> jsHeaders[key] = value }
        options.headers = jsHeaders

        if (body != null)
            options.body = json.encodeToString(requestClass.serializer(), body)

        val url = baseUrl.removeSuffix("/") + "/" + path.removePrefix("/")
        val response = fetch(url, options).await()
        val body = (response.json() as Promise<dynamic>).await()

        val bodyContent = JSON.stringify(body)

        require(response.ok) { "Failed to execute action: ${response.status} - $bodyContent" }

        return json.decodeFromString(responseClass.serializer(), bodyContent)
    }
}
