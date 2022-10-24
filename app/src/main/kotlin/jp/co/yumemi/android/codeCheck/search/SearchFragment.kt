/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.ktor.client.*
import io.ktor.client.engine.android.*
import jp.co.yumemi.android.codeCheck.R
import jp.co.yumemi.android.codeCheck.data.GitHubApi
import jp.co.yumemi.android.codeCheck.data.GitHubApiImpl
import jp.co.yumemi.android.codeCheck.data.GitHubRepositoryImpl
import jp.co.yumemi.android.codeCheck.databinding.FragmentSearchBinding
import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels {
        val client = HttpClient(Android)
        val api: GitHubApi = GitHubApiImpl(client)
        val repository = GitHubRepositoryImpl(api)
        SearchViewModel.provideFactory(repository)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSearchBinding.bind(view)
        val layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = context?.let {
            DividerItemDecoration(it, layoutManager.orientation)
        }
        val adapter = SearchListAdapter(object : SearchListAdapter.OnItemClickListener {
            override fun itemClick(item: GitHubRepositoryItem) {
                gotoRepositoryFragment(item)
            }
        })

        binding.run {
            searchInputText.setOnEditorActionListener { editText, action, _ ->
                if (action == EditorInfo.IME_ACTION_SEARCH) {
                    editText.text.toString().let {
                        viewModel.searchResults(it)
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
            recyclerView.also {
                it.layoutManager = layoutManager
                dividerItemDecoration?.run {
                    it.addItemDecoration(this)
                }
                it.adapter = adapter
            }
        }

        viewModel.repositoryItems.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun gotoRepositoryFragment(item: jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem) {
        val action = SearchFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

