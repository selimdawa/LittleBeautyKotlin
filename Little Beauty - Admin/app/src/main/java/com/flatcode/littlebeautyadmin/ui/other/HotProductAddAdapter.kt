package com.flatcode.littlebeautyadmin.ui.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlebeautyadmin.R
import com.flatcode.littlebeautyadmin.databinding.ItemProductAddBinding
import com.flatcode.littlebeautyadmin.model.Post
import com.flatcode.littlebeautyadmin.ui.post.PostDetailsActivity
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.loadImage
import com.flatcode.littlebeautyadmin.utils.openActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotProductAddAdapter(
    private val mContext: Context, private val listener: OnItemClickListener
) : ListAdapter<Post, HotProductAddAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onAddClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductAddBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return
        val id = post.postid

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
        nrLikes(holder, id)
        holder.add.setOnClickListener { listener.onAddClick(post) }
        holder.card.setOnClickListener {
            mContext.openActivity<PostDetailsActivity>(DATA.POST_ID to id)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.clearListeners()
    }

    class ViewHolder(binding: ItemProductAddBinding) : RecyclerView.ViewHolder(binding.root) {
        val card: MaterialCardView = binding.card
        val imageProduct: ImageView = binding.imageProduct
        val likes: TextView = binding.likes
        val name: TextView = binding.name
        val price: TextView = binding.price
        val add: ImageButton = binding.add

        private var likesRef: com.google.firebase.database.DatabaseReference? = null
        private var likesListener: ValueEventListener? = null

        fun bindLikes(ref: com.google.firebase.database.DatabaseReference, listener: ValueEventListener) {
            likesRef = ref
            likesListener = listener
            ref.addValueEventListener(listener)
        }

        fun clearListeners() {
            likesListener?.let { likesRef?.removeEventListener(it) }
            likesRef = null
            likesListener = null
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

    companion object DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postid == newItem.postid
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
