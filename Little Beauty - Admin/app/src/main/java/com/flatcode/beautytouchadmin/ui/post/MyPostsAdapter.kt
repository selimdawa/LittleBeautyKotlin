package com.flatcode.beautytouchadmin.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.glide
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ItemMyPostBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class MyPostsAdapter(
    private val mContext: Context, 
    private val listener: OnItemClickListener
) : ListAdapter<Post, MyPostsAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onMoreClick(post: Post)
        fun onLikeClick(post: Post, isLiked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyPostBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return

        holder.image_product.glide(false, post.postimage)
        if (post.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = post.name
        }
        if (post.price == DATA.EMPTY) {
            holder.price.visibility = View.GONE
        } else {
            holder.price.visibility = View.VISIBLE
            holder.price.text = MessageFormat.format("{0} $", post.price)
        }

        nrLikes(holder.likes, post.postid)
        isLiked(post.postid, holder.like)
        
        holder.like.setOnClickListener {
            val isCurrentlyLiked = holder.like.tag == "liked"
            listener.onLikeClick(post, isCurrentlyLiked)
        }

        holder.more.setOnClickListener { listener.onMoreClick(post) }
        holder.card.setOnClickListener {
            mContext.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid)
        }
    }

    class ViewHolder(binding: ItemMyPostBinding) : RecyclerView.ViewHolder(binding.root) {
        var image_product: ImageView = binding.imageProduct
        var more: ImageView = binding.more
        var like: ImageView = binding.like
        var likes: TextView = binding.likes
        var name: TextView = binding.name
        var price: TextView = binding.price
        var card: MaterialCardView = binding.card
    }

    private fun nrLikes(likes: TextView, postId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun isLiked(postId: String?, imageView: ImageView) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.LIKES).child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                    imageView.setImageResource(R.drawable.ic_heart_selected)
                    imageView.tag = "liked"
                } else {
                    imageView.setImageResource(R.drawable.ic_heart_unselected)
                    imageView.tag = "like"
                }
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
