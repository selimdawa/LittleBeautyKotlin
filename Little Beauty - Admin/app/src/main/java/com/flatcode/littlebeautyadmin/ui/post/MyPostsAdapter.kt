package com.flatcode.littlebeautyadmin.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlebeautyadmin.R
import com.flatcode.littlebeautyadmin.databinding.ItemMyPostBinding
import com.flatcode.littlebeautyadmin.model.Post
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.loadImage
import com.flatcode.littlebeautyadmin.utils.openActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyPostsAdapter(
    private val mContext: Context, private val listener: OnItemClickListener
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

        holder.imageProduct.loadImage(false, post.postimage)
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
            holder.price.text = mContext.getString(R.string.price_format, post.price)
        }

        holder.clearListeners()
        nrLikes(holder, post.postid)
        isLiked(holder, post.postid)

        holder.like.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                getItem(currentPos)?.let { currentPost ->
                    val isCurrentlyLiked = holder.like.tag == "liked"
                    listener.onLikeClick(currentPost, isCurrentlyLiked)
                }
            }
        }

        holder.more.setOnClickListener { listener.onMoreClick(post) }
        holder.card.setOnClickListener {
            mContext.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clearListeners()
    }

    class ViewHolder(binding: ItemMyPostBinding) : RecyclerView.ViewHolder(binding.root) {
        var imageProduct: ImageView = binding.imageProduct
        var more: ImageView = binding.more
        var like: ImageView = binding.like
        var likes: TextView = binding.likes
        var name: TextView = binding.name
        var price: TextView = binding.price
        var card: MaterialCardView = binding.card

        private var likesRef: com.google.firebase.database.DatabaseReference? = null
        private var likesListener: ValueEventListener? = null
        private var likeRef: com.google.firebase.database.DatabaseReference? = null
        private var likeListener: ValueEventListener? = null

        fun bindLikes(ref: com.google.firebase.database.DatabaseReference, listener: ValueEventListener) {
            likesRef = ref
            likesListener = listener
            ref.addValueEventListener(listener)
        }

        fun bindIsLiked(ref: com.google.firebase.database.DatabaseReference, listener: ValueEventListener) {
            likeRef = ref
            likeListener = listener
            ref.addValueEventListener(listener)
        }

        fun clearListeners() {
            likesListener?.let { likesRef?.removeEventListener(it) }
            likeListener?.let { likeRef?.removeEventListener(it) }
            likesRef = null
            likesListener = null
            likeRef = null
            likeListener = null
        }
    }

    private fun nrLikes(holder: ViewHolder, postId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                holder.likes.text = dataSnapshot.childrenCount.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        holder.bindLikes(reference, listener)
    }

    private fun isLiked(holder: ViewHolder, postId: String) {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.LIKES).child(postId)
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(DATA.FirebaseUserUid).exists()) {
                    holder.like.setImageResource(R.drawable.ic_heart_selected)
                    holder.like.tag = "liked"
                } else {
                    holder.like.setImageResource(R.drawable.ic_heart_unselected)
                    holder.like.tag = "like"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        holder.bindIsLiked(reference, listener)
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
