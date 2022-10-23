package jp.co.yumemi.android.codeCheck

interface GitHubRepository {
    suspend fun searchRepository(query: String): Result<List<GitHubRepositoryItem>>
}