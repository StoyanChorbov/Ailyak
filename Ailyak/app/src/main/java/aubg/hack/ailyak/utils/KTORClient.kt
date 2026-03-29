package aubg.hack.ailyak.utils

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

object KtorClient {
    val instance = HttpClient(CIO)

    suspend fun get(url: String, params: Map<String, String> = emptyMap()): Result<String> {
        return try {
            val response: HttpResponse = instance.get(url) {
                params.forEach { (k, v) -> parameter(k, v) }
            }
            Result.success(response.bodyAsText())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}