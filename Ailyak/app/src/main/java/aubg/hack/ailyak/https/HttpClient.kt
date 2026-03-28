package aubg.hack.ailyak.https

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object HttpClient {

    fun get(url: String, params: Map<String, String> = emptyMap()): String {
        val query = params.entries.joinToString("&") { (k, v) ->
            "${k}=${URLEncoder.encode(v, "UTF-8")}"
        }
        val fullUrl = if (query.isNotEmpty()) "$url?$query" else url

        val connection = URL(fullUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Accept", "application/json")
        connection.connectTimeout = 5_000
        connection.readTimeout = 10_000

        return try {
            BufferedReader(InputStreamReader(connection.inputStream))
                .use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }
}
