package com.flatcode.beautytouchadmin.ui.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.Application
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.ui.ads.ADsInfoActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ItemAdsUserBinding

class ADsUserAdapter(
    private val context: Context, initialList: MutableList<User?>
) : ListAdapter<User, ADsUserAdapter.ViewHolder>(DiffCallback), Filterable {

    var list: MutableList<User?> = initialList
        set(value) {
            field = value
            submitList(value.filterNotNull())
        }

    var filterList: MutableList<User?> = initialList

    init {
        submitList(initialList.filterNotNull())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdsUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position] ?: return
        val userId = item.id
        val username = item.username ?: ""
        val profileImage = item.imageurl ?: ""
        val timestamp = item.started?.toLongOrNull() ?: 0L
        val adLoaded = item.adLoad
        val adClicked = item.adClick
        val formattedDate: String = Application.formatTimestamp(timestamp)

        holder.profileImage.loadImage(true, profileImage)
        if (username.isNullOrEmpty()) {
            holder.username.visibility = View.GONE
        } else {
            holder.username.visibility = View.VISIBLE
            holder.username.text = username
        }

        val rankValue = list.size - position
        holder.time.text = formattedDate
        holder.rank.text = "$rankValue"
        holder.numberADsLoad.text = "$adLoaded"
        holder.numberADsClick.text = "$adClicked"

        holder.item.setOnClickListener {
            context.openActivity<ADsInfoActivity>(DATA.PROFILE_ID to userId)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (!constraint.isNullOrEmpty()) {
                    val constraintStr = constraint.toString().uppercase()
                    val filter = mutableListOf<User?>()
                    for (item in filterList) {
                        if (item?.username?.uppercase()?.contains(constraintStr) == true) {
                            filter.add(item)
                        }
                    }
                    results.count = filter.size
                    results.values = filter
                } else {
                    results.count = filterList.size
                    results.values = filterList
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                @Suppress("UNCHECKED_CAST") submitList(results.values as MutableList<User>)
            }
        }
    }

    class ViewHolder(binding: ItemAdsUserBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: ImageView = binding.profileImage
        val username: TextView = binding.username
        val rank: TextView = binding.rank
        val numberADsLoad: TextView = binding.numberADsLoad
        val numberADsClick: TextView = binding.numberADsClick
        val time: TextView = binding.time
        val item: LinearLayout = binding.item
    }

    companion object DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}