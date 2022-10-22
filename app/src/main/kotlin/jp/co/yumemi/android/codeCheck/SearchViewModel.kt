/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.codeCheck.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SearchViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    fun searchResults(inputText: String): List<GitHubRepositoryItem> = runBlocking {
        val client = HttpClient(Android)

        return@runBlocking GlobalScope.async {
            val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", inputText)
            }
            val jsonBody = JSONObject(response.receive<String>())
            val jsonItems: JSONArray? = jsonBody.optJSONArray("items")
            val items = mutableListOf<GitHubRepositoryItem>()

            jsonItems?.let { itemArray ->
                for (i in 0 until itemArray.length()) {
                    val jsonItem: JSONObject? = itemArray.optJSONObject(i)
                    val name = jsonItem?.optString("full_name") ?: ""
                    val ownerIconUrl =
                        jsonItem?.optJSONObject("owner")?.optString("avatar_url") ?: ""
                    val language = jsonItem?.optString("language") ?: ""
                    val stargazersCount = jsonItem?.optLong("stargazers_count") ?: 0L
                    val watchersCount = jsonItem?.optLong("watchers_count") ?: 0L
                    val forksCount = jsonItem?.optLong("forks_count") ?: 0L
                    val openIssuesCount = jsonItem?.optLong("open_issues_count") ?: 0L

                    items.add(
                        GitHubRepositoryItem(
                            name = name,
                            ownerIconUrl = ownerIconUrl,
                            language = app.getString(R.string.written_language, language),
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount
                        )
                    )
                }
            }

            lastSearchDate = Date()

            return@async items.toList()
        }.await()
    }
}

@Parcelize
data class GitHubRepositoryItem(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable