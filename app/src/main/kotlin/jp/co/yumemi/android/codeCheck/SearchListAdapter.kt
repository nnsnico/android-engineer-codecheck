package jp.co.yumemi.android.codeCheck

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.yumemi.android.codeCheck.databinding.LayoutItemBinding
import jp.co.yumemi.android.codeCheck.model.GitHubRepositoryItem

class SearchListAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<GitHubRepositoryItem, SearchListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(
        private val binding: LayoutItemBinding,
        private val itemClickListener: OnItemClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GitHubRepositoryItem) {
            binding.repositoryNameView.run {
                text = item.fullName
                setOnClickListener {
                    itemClickListener.itemClick(item)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun itemClick(item: GitHubRepositoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = LayoutItemBinding.inflate(view, parent, false)
        return ViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object DiffCallback : DiffUtil.ItemCallback<GitHubRepositoryItem>() {
        override fun areItemsTheSame(
            oldItem: GitHubRepositoryItem,
            newItem: GitHubRepositoryItem
        ): Boolean {
            return oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(
            oldItem: GitHubRepositoryItem,
            newItem: GitHubRepositoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
