package com.flatcode.beautytouch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ItemProductGridBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import java.text.MessageFormat

class ProductsStaggeredAdapter(
    private val onItemClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onSaveClick: (Post) -> Unit
) : ListAdapter<Post, ProductsStaggeredAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return
        val context = holder.itemView.context

        with(holder.binding) {
            imageProduct.Glide(false, context, post.postimage)
            name.apply {
                visibility = if (post.name == DATA.EMPTY) View.GONE else View.VISIBLE
                text = post.name
            }
            price.apply {
                visibility = if (post.price == DATA.EMPTY) View.GONE else View.VISIBLE
                text = MessageFormat.format("{0} SYP", post.price)
            }

            // Bind status from model
            like.setImageResource(if (post.isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
            like.tag = if (post.isLiked) "liked" else "like"

            save.setImageResource(if (post.isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
            save.tag = if (post.isSaved) "saved" else "save"

            likes.text = MessageFormat.format("{0}", post.nrLikes)

            like.setOnClickListener { onLikeClick(post) }
            save.setOnClickListener { onSaveClick(post) }
            card.setOnClickListener { onItemClick(post) }
        }
    }

    class ViewHolder(val binding: ItemProductGridBinding) : RecyclerView.ViewHolder(binding.root)

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.postid == newItem.postid
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}