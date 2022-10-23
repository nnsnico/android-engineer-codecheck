/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.codeCheck.databinding.FragmentSearchBinding

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()

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

    private fun gotoRepositoryFragment(item: GitHubRepositoryItem) {
        val action = SearchFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

