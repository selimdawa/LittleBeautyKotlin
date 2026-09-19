package com.flatcode.beautytouch.ui.adapter

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
import com.flatcode.beautytouch.databinding.ItemLeaderboardBinding
import com.flatcode.beautytouch.filter.LeaderboardFilter
import com.flatcode.beautytouch.model.Tools
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class LeaderboardAdapter(private val mContext: Context) :
    ListAdapter<User, LeaderboardAdapter.ViewHolder>(UserDiffCallback()), Filterable {

    var filterList: ArrayList<User?> = ArrayList()
    private var filter: LeaderboardFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLeaderboardBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = getItem(position) ?: return
        val id = user.id
        val username = user.username
        val image = user.imageurl
        val reversePosition = itemCount - position

        holder.range.text = MessageFormat.format("{0}", reversePosition)
        holder.image_profile.Glide(true, mContext, image)

        if (username == DATA.EMPTY) {
            holder.username.visibility = View.GONE
        } else {
            holder.username.visibility = View.VISIBLE
            holder.username.text = username
        }
        sessionInfo(holder.points, id)
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = LeaderboardFilter(filterList, this)
        }
        return filter!!
    }

    class ViewHolder(val binding: ItemLeaderboardBinding) : RecyclerView.ViewHolder(binding.root) {
        val image_profile: ImageView = binding.imageProfile
        val username: TextView = binding.username
        val range: TextView = binding.range
        val points: TextView = binding.points
        val linear_one: LinearLayout = binding.linearOne
    }

    private fun sessionInfo(points: TextView, id: String?) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.M_TOOLS)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tools = dataSnapshot.getValue(Tools::class.java)!!
                val year = tools.year
                val session = tools.sessionNumber
                points(year, session, points, id)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun points(year: String?, session: String?, points: TextView, id: String?) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.USERS).child(id!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val key = year + "_" + session
                val value = DATA.EMPTY + dataSnapshot.child(key).value
                if (dataSnapshot.child(key).exists()) points.text =
                    MessageFormat.format("{0}", value) else points.text = "0"
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}