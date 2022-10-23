/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.app.Application
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.codeCheck.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SearchViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val _repositoryItems: MutableLiveData<List<GitHubRepositoryItem>> = MutableLiveData()
    val repositoryItems: LiveData<List<GitHubRepositoryItem>> = _repositoryItems

    fun searchResults(inputText: String) {
        viewModelScope.launch {
            val client = HttpClient(Android)

            val response: HttpResponse? = runCatching {
                client.get<HttpResponse>("https://api.github.com/search/repositories") {
                    header("Accept", "application/vnd.github.v3+json")
                    parameter("q", inputText)
                }
            }.getOrElse {
                Log.d(this@SearchViewModel.javaClass.simpleName, "Failed to load: $it")
                null
            }

            val jsonBody = response?.receive<String>()?.let { JSONObject(it) }
            val jsonItems: JSONArray? = jsonBody?.optJSONArray("items")

            jsonItems?.let { itemArray ->
                _repositoryItems.value = (0 until itemArray.length()).map { i ->
                    val jsonItem: JSONObject? = itemArray.optJSONObject(i)
                    val name = jsonItem?.optString("full_name") ?: ""
                    val ownerIconUrl =
                        jsonItem?.optJSONObject("owner")?.optString("avatar_url") ?: ""
                    val language = jsonItem?.optString("language") ?: ""
                    val stargazersCount = jsonItem?.optLong("stargazers_count") ?: 0L
                    val watchersCount = jsonItem?.optLong("watchers_count") ?: 0L
                    val forksCount = jsonItem?.optLong("forks_count") ?: 0L
                    val openIssuesCount = jsonItem?.optLong("open_issues_count") ?: 0L

                    GitHubRepositoryItem(
                        name = name,
                        ownerIconUrl = ownerIconUrl,
                        language = app.getString(R.string.written_language, language),
                        stargazersCount = stargazersCount,
                        watchersCount = watchersCount,
                        forksCount = forksCount,
                        openIssuesCount = openIssuesCount
                    )
                }
                lastSearchDate = Date()
            }
        }
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