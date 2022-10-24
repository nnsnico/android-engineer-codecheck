/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.android.*
import jp.co.yumemi.android.codeCheck.data.GitHubApi
import jp.co.yumemi.android.codeCheck.data.GitHubApiImpl
import jp.co.yumemi.android.codeCheck.data.GitHubRepositoryImpl
import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem
import jp.co.yumemi.android.codeCheck.model.repository.GitHubRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    // TODO inject from constructor by DI
    private val client = HttpClient(Android)
    private val api: GitHubApi = GitHubApiImpl(client)
    private val repository: GitHubRepository = GitHubRepositoryImpl(api)

    private val _repositoryItems: MutableLiveData<List<GitHubRepositoryItem>> = MutableLiveData()
    val repositoryItems: LiveData<List<GitHubRepositoryItem>> = _repositoryItems

    fun searchResults(inputText: String) {
        viewModelScope.launch {
            repository.searchRepository(inputText).fold(
                onSuccess = {
                    _repositoryItems.value = it
                },
                onFailure = {
                    Log.d(this@SearchViewModel.javaClass.simpleName, it.stackTraceToString())
                },
            )
        }
    }
}