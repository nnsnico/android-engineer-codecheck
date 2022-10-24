package jp.co.yumemi.android.codeCheck.data

import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem
import jp.co.yumemi.android.codeCheck.model.repository.GitHubRepository
import org.json.JSONObject

class GitHubRepositoryImpl(private val api: GitHubApi): GitHubRepository {
    override suspend fun searchRepository(query: String): Result<List<GitHubRepositoryItem>> {
        val result = api.search(query)
        return result.mapCatching {
            val jsonItems = it.getJSONArray("items")

            (0 until jsonItems.length()).map { i ->
                val jsonItem: JSONObject = jsonItems.getJSONObject(i)
                val name = jsonItem.getString("full_name")
                val ownerIconUrl = jsonItem
                    .getJSONObject("owner")
                    .getString("avatar_url")
                val language = jsonItem.getString("language")
                val stargazersCount = jsonItem.getLong("stargazers_count")
                val watchersCount = jsonItem.getLong("watchers_count")
                val forksCount = jsonItem.getLong("forks_count")
                val openIssuesCount = jsonItem.getLong("open_issues_count")

                GitHubRepositoryItem(
                    fullName = name,
                    avatarUrl = ownerIconUrl,
                    language = language,
                    stargazersCount = stargazersCount,
                    watchersCount = watchersCount,
                    forksCount = forksCount,
                    openIssuesCount = openIssuesCount
                )
            }
        }
    }
}