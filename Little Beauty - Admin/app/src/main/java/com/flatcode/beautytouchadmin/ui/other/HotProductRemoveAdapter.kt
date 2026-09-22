package com.flatcode.beautytouchadmin.ui.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.ui.post.PostDetailsActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ItemProductRemoveBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotProductRemoveAdapter(
    private val mContext: Context, 
    private val listener: OnItemClickListener
) : ListAdapter<Post, HotProductRemoveAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onRemoveClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductRemoveBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return
        val id = post.postid

        holder.image_product.loadImage(false, post.postimage)
        if (post.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = post.name
        }
        if (post.price.isNullOrEmpty()) {
            holder.price.visibility = View.GONE
        } else {
            holder.price.visibility = View.VISIBLE
            holder.price.text = "${post.price} $"
        }

        nrLikes(holder.likes, id)
        holder.remove.setOnClickListener { listener.onRemoveClick(post) }
        holder.card.setOnClickListener {
            mContext.openActivity<PostDetailsActivity>(DATA.POST_ID to id)
        }
    }

    class ViewHolder(binding: ItemProductRemoveBinding) : RecyclerView.ViewHolder(binding.root) {
        val card: MaterialCardView = binding.card
        val image_product: ImageView = binding.imageProduct
        val likes: TextView = binding.likes
        val name: TextView = binding.name
        val price: TextView = binding.price
        val remove: ImageButton = binding.remove
    }

    private fun nrLikes(likes: TextView, postId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = "${dataSnapshot.childrenCount}"
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
