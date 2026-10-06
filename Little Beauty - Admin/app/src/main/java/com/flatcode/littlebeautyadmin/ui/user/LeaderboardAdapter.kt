package com.flatcode.littlebeautyadmin.ui.user

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
import com.flatcode.littlebeautyadmin.databinding.ItemLeaderboradBinding
import com.flatcode.littlebeautyadmin.model.User
import com.flatcode.littlebeautyadmin.ui.ads.ADsInfoActivity
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.loadImage
import com.flatcode.littlebeautyadmin.utils.openActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderboardAdapter(
    private val mContext: Context, private val pointsKey: String? = null
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

        holder.clearListeners()
        if (pointsKey != null) {
            fetchPoints(holder, pointsKey, id)
        }

        holder.item.setOnClickListener {
            mContext.openActivity<ADsInfoActivity>(DATA.PROFILE_ID to id)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clearListeners()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (originalList.isEmpty() && currentList.isNotEmpty()) {
                    originalList = ArrayList(currentList)
                }

                if (!constraint.isNullOrEmpty()) {
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
                submitList((results.values as? List<*>)?.filterIsInstance<User>())
            }
        }
    }

    class ViewHolder(binding: ItemLeaderboradBinding) : RecyclerView.ViewHolder(binding.root) {
        val profileImage: ImageView = binding.profileImage
        val username: TextView = binding.username
        val numberADsLoad: TextView = binding.numberADsLoad
        val rank: TextView = binding.rank
        val item: LinearLayout = binding.item

        private var pointsRef: com.google.firebase.database.DatabaseReference? = null
        private var pointsListener: ValueEventListener? = null

        fun bindPoints(ref: com.google.firebase.database.DatabaseReference, listener: ValueEventListener) {
            pointsRef = ref
            pointsListener = listener
            ref.addValueEventListener(listener)
        }

        fun clearListeners() {
            pointsListener?.let { pointsRef?.removeEventListener(it) }
            pointsRef = null
            pointsListener = null
        }
    }

    private fun fetchPoints(holder: ViewHolder, key: String, id: String) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(id)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.child(key).value?.toString() ?: "0"
                if (dataSnapshot.child(key).exists()) {
                    holder.numberADsLoad.text = value
                } else {
                    holder.numberADsLoad.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        holder.bindPoints(reference, listener)
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
