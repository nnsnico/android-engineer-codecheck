package jp.co.yumemi.android.codeCheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SearchListAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<GitHubRepositoryItem, SearchListAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun itemClick(item: GitHubRepositoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.itemView.findViewById<TextView>(R.id.repositoryNameView).text = item.name
        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(item)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<GitHubRepositoryItem>() {
        override fun areItemsTheSame(
            oldItem: GitHubRepositoryItem,
            newItem: GitHubRepositoryItem
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: GitHubRepositoryItem,
            newItem: GitHubRepositoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
