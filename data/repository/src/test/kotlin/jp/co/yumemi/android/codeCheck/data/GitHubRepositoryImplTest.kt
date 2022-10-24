package jp.co.yumemi.android.codeCheck.data

import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class GitHubRepositoryImplTest {
    private val sampleResponse = JSONObject(
        mapOf(
            "items" to JSONArray(
                listOf(
                    JSONObject(
                        mapOf(
                            "full_name" to "Jetbrains/kotlin",
                            "owner" to JSONObject(
                                mapOf(
                                    "avatar_url" to "https://secure.gravatar.com/avatar/e7956084e75f239de85d3a31bc172ace?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png"
                                )
                            ),
                            "language" to "Kotlin",
                            "stargazers_count" to 38530,
                            "watchers_count" to 38530,
                            "forks_count" to 4675,
                            "open_issues_count" to 131
                        )
                    )
                )
            )
        )
    )

    @Test
    fun success_WhenApiResultIsSuccess() {
        runBlocking {
            val mockApi = mock<GitHubApi> {
                onBlocking { search("test") }.doReturn(Result.success(sampleResponse))
            }
            val repository = GitHubRepositoryImpl(mockApi)
            val result = repository.searchRepository("test")

            assertEquals(
                result,
                Result.success(
                    listOf(
                        GitHubRepositoryItem(
                            fullName = "Jetbrains/kotlin",
                            avatarUrl = "https://secure.gravatar.com/avatar/e7956084e75f239de85d3a31bc172ace?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png",
                            language = "Kotlin",
                            stargazersCount = 38530,
                            watchersCount = 38530,
                            forksCount = 4675,
                            openIssuesCount = 131,
                        )
                    )
                )
            )
        }
    }

    @Test
    fun fail_WhenApiResultIsFailure() {
        runBlocking {
            val mockApi = mock<GitHubApi> {
                onBlocking { search("test") } doReturn Result.failure(Throwable("failed for test"))
            }
            val repository = GitHubRepositoryImpl(mockApi)
            val result = repository.searchRepository("test")

            assertEquals(result.isFailure, true)
        }
    }

    @Test
    fun fail_WhenApiResponseIsInvalidJSON() {
        runBlocking {
            val mockApi = mock<GitHubApi> {
                onBlocking { search("test") } doReturn Result.success(JSONObject(mapOf("test" to 1)))
            }
            val repository = GitHubRepositoryImpl(mockApi)
            val result = repository.searchRepository("test")

            assertEquals(result.isFailure, true)
        }
    }
}