package com.flatcode.beautytouch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.databinding.ItemLeaderboardBinding
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.loadImage
import java.util.Locale

class LeaderboardAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, LeaderboardAdapter.ViewHolder>(UserDiffCallback()), Filterable {

    var filterList: List<User> = emptyList()
    private var filter: LeaderboardFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position) ?: return
        val rank = position + 1 // Displaying actual rank instead of reverse position which was confusing

        with(holder.binding) {
            range.text = "$rank"
            imageProfile.loadImage(true, user.imageurl)

            username.apply {
                visibility = if (user.username == DATA.EMPTY) View.GONE else View.VISIBLE
                text = user.username
            }
            points.text = "${user.points}"
            
            root.setOnClickListener { onItemClick(user) }
        }
    }

    override fun getFilter(): Filter {
        return filter ?: LeaderboardFilter(filterList, this).also { filter = it }
    }

    inner class LeaderboardFilter(var list: List<User>, var adapter: LeaderboardAdapter) : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (!constraint.isNullOrEmpty()) {
                val constraintStr = constraint.toString().uppercase(Locale.getDefault())
                val filterResults = list.filter {
                    it.username?.uppercase(Locale.getDefault())?.contains(constraintStr) == true
                }
                results.count = filterResults.size
                results.values = filterResults
            } else {
                results.count = list.size
                results.values = list
            }
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            adapter.submitList(results?.values as? List<User>)
        }
    }

    class ViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root)

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}