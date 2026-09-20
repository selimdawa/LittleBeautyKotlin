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
import com.flatcode.beautytouch.filter.LeaderboardFilter
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import java.text.MessageFormat

class LeaderboardAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, LeaderboardAdapter.ViewHolder>(UserDiffCallback()), Filterable {

    var filterList: ArrayList<User?> = ArrayList()
    private var filter: LeaderboardFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position) ?: return
        val context = holder.itemView.context
        val rank = position + 1 // Displaying actual rank instead of reverse position which was confusing

        with(holder.binding) {
            range.text = MessageFormat.format("{0}", rank)
            imageProfile.Glide(true, context, user.imageurl)

            username.apply {
                visibility = if (user.username == DATA.EMPTY) View.GONE else View.VISIBLE
                text = user.username
            }
            points.text = MessageFormat.format("{0}", user.points)
            
            root.setOnClickListener { onItemClick(user) }
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = LeaderboardFilter(filterList, this)
        }
        return filter!!
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