package com.flatcode.beautytouchadmin.ui.user

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
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.ui.ads.ADsInfoActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ItemLeaderboradBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderboardAdapter(
    private val mContext: Context, 
    var isUser: Boolean,
    private val pointsKey: String? = null
) : ListAdapter<User, LeaderboardAdapter.ViewHolder>(DiffCallback), Filterable {

    private var originalList: List<User> = currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboradBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val id = item.id
        val username = item.username ?: ""
        val image = item.imageurl ?: ""
        val rankValue = itemCount - position

        holder.rank.text = "$rankValue"
        holder.profileImage.loadImage(true, image)
        if (username.isEmpty()) {
            holder.username.visibility = View.GONE
        } else {
            holder.username.visibility = View.VISIBLE
            holder.username.text = username
        }

        if (pointsKey != null) {
            fetchPoints(pointsKey, holder.numberADsLoad, id)
        }

        holder.item.setOnClickListener {
            mContext.openActivity<ADsInfoActivity>(DATA.PROFILE_ID to id)
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (originalList.isEmpty() && currentList.isNotEmpty()) {
                    originalList = ArrayList(currentList)
                }
                
                if (constraint != null && constraint.isNotEmpty()) {
                    val constraintStr = constraint.toString().uppercase()
                    val filter = mutableListOf<User>()
                    for (item in originalList) {
                        if (item.username?.uppercase()?.contains(constraintStr) == true) {
                            filter.add(item)
                        }
                    }
                    results.count = filter.size
                    results.values = filter
                } else {
                    results.count = originalList.size
                    results.values = originalList
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                submitList(results.values as MutableList<User>)
            }
        }
    }

    class ViewHolder(binding: ItemLeaderboradBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: ImageView = binding.profileImage
        val username: TextView = binding.username
        val numberADsLoad: TextView = binding.numberADsLoad
        val rank: TextView = binding.rank
        val item: LinearLayout = binding.item
    }

    private fun fetchPoints(key: String, points: TextView, id: String) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(id)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.child(key).value?.toString() ?: "0"
                if (dataSnapshot.child(key).exists()) {
                    points.text = value
                } else {
                    points.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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
