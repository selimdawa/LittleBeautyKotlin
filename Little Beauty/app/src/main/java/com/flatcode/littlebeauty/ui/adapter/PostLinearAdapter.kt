package com.flatcode.littlebeauty.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlebeauty.R
import com.flatcode.littlebeauty.databinding.ItemProductLinearBinding
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.utils.DATA
import com.flatcode.littlebeauty.utils.loadImage

class PostLinearAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onSaveClick: (Post) -> Unit
) : ListAdapter<Post, PostLinearAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val post = getItem(position) ?: return
            val changes = payloads.flatMap { (it as? Set<*>)?.filterIsInstance<String>() ?: emptyList() }
            with(holder.binding) {
                if ("LIKE" in changes) {
                    like.setImageResource(if (post.isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
                    like.tag = if (post.isLiked) "liked" else "like"
                    likes.text = post.nrLikes.toString()
                    like.setOnClickListener {
                        val currentPos = holder.bindingAdapterPosition
                        if (currentPos != RecyclerView.NO_POSITION) {
                            getItem(currentPos)?.let { currentPost -> onLikeClick(currentPost) }
                        }
                    }
                }
                if ("SAVE" in changes) {
                    save.setImageResource(if (post.isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
                    save.tag = if (post.isSaved) "saved" else "save"
                    save.setOnClickListener {
                        val currentPos = holder.bindingAdapterPosition
                        if (currentPos != RecyclerView.NO_POSITION) {
                            getItem(currentPos)?.let { currentPost -> onSaveClick(currentPost) }
                        }
                    }
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return

        with(holder.binding) {
            imageProduct.loadImage(false, post.postimage)
            name.apply {
                visibility = if (post.name == DATA.EMPTY) View.GONE else View.VISIBLE
                text = post.name
            }
            price.apply {
                visibility = if (post.price == DATA.EMPTY) View.GONE else View.VISIBLE
                text = context.getString(R.string.price_format, post.price)
            }

            // Bind status from model
            like.setImageResource(if (post.isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
            like.tag = if (post.isLiked) "liked" else "like"

            save.setImageResource(if (post.isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
            save.tag = if (post.isSaved) "saved" else "save"

            likes.text = post.nrLikes.toString()

            like.setOnClickListener {
                val currentPos = holder.bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    getItem(currentPos)?.let { currentPost -> onLikeClick(currentPost) }
                }
            }
            save.setOnClickListener {
                val currentPos = holder.bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    getItem(currentPos)?.let { currentPost -> onSaveClick(currentPost) }
                }
            }
            card.setOnClickListener {
                val currentPos = holder.bindingAdapterPosition
                if (currentPos != RecyclerView.NO_POSITION) {
                    getItem(currentPos)?.let { currentPost -> onItemClick(currentPost) }
                }
            }
        }
    }

    class ViewHolder(val binding: ItemProductLinearBinding) : RecyclerView.ViewHolder(binding.root)

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postid == newItem.postid
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Post, newItem: Post): Any? {
            val diff = mutableSetOf<String>()
            if (oldItem.isLiked != newItem.isLiked || oldItem.nrLikes != newItem.nrLikes) {
                diff.add("LIKE")
            }
            if (oldItem.isSaved != newItem.isSaved) {
                diff.add("SAVE")
            }
            return diff.ifEmpty { super.getChangePayload(oldItem, newItem) }
        }
    }
}