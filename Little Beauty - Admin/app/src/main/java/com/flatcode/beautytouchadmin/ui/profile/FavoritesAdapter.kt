package com.flatcode.beautytouchadmin.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ItemProductLinearBinding
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.ui.post.PostDetailsActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.openActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoritesAdapter(private val mContext: Context, initialList: MutableList<Post?>) :
    ListAdapter<Post, FavoritesAdapter.ViewHolder>(DiffCallback) {

    var list: MutableList<Post?> = initialList
        set(value) {
            field = value
            submitList(value.filterNotNull())
        }

    init {
        submitList(initialList.filterNotNull())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductLinearBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return
        val id = post.postid

        holder.imageProduct.loadImage(true, post.postimage)
        if (post.name.isNullOrEmpty()) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = post.name
        }
        if (post.price.isNullOrEmpty()) {
            holder.price.visibility = View.GONE
        } else {
            holder.price.visibility = View.VISIBLE
            holder.price.text = mContext.getString(R.string.price_format, post.price)
        }

        nrLikes(holder.likes, post.postid)
        holder.card.setOnClickListener {
            mContext.openActivity<PostDetailsActivity>(DATA.POST_ID to id)
        }
    }

    class ViewHolder(binding: ItemProductLinearBinding) : RecyclerView.ViewHolder(binding.root) {
        val card: MaterialCardView = binding.card
        val imageProduct: ImageView = binding.imageProduct
        val like: ImageView = binding.like
        val likes: TextView = binding.likes
        val name: TextView = binding.name
        val price: TextView = binding.price
    }

    private fun nrLikes(likes: TextView, postId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postid == newItem.postid
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
