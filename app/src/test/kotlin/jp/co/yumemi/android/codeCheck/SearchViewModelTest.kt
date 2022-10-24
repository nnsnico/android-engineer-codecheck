package jp.co.yumemi.android.codeCheck

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem
import jp.co.yumemi.android.codeCheck.model.repository.GitHubRepository
import jp.co.yumemi.android.codeCheck.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class SearchViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun repositoryItems_updateSuccessfully() {
        val sample = listOf(
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

        val mockRepository = mock<GitHubRepository> {
            onBlocking { searchRepository("test") } doReturn Result.success(sample)
        }
        val viewModel = SearchViewModel(mockRepository)
        val testObserver = spy<Observer<List<GitHubRepositoryItem>>>()
        viewModel.repositoryItems.observeForever(testObserver)

        viewModel.searchResults("test")
        verify(testObserver).onChanged(sample)
    }

    @Test
    fun repositoryItems_notUpdate() {
        val mockRepository = mock<GitHubRepository> {
            onBlocking { searchRepository("test") } doReturn Result.failure(Throwable("failed for test"))
        }
        val viewModel = SearchViewModel(mockRepository)
        val testObserver = spy<Observer<List<GitHubRepositoryItem>>>()
        viewModel.repositoryItems.observeForever(testObserver)

        viewModel.searchResults("test")
        verifyNoInteractions(testObserver)
    }
}