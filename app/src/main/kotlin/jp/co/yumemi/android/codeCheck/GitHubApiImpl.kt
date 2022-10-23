package jp.co.yumemi.android.codeCheck

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import org.json.JSONObject

class GitHubApiImpl(private val client: HttpClient) : GitHubApi {
    override suspend fun search(query: String): Result<JSONObject> {
        return runCatching {
            val response = client.get<HttpResponse>("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", query)
            }
            val jsonBody = JSONObject(response.receive<String>())
            Result.success(jsonBody)
        }.getOrElse {
            Result.failure(it)
        }
    }
}