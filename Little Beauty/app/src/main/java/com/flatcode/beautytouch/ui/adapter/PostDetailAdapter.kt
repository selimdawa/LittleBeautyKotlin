package com.flatcode.beautytouch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ItemPostDetailBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.loadImage
import java.text.MessageFormat

class PostDetailAdapter(
    private val onLikeClick: (Post) -> Unit,
    private val onSaveClick: (Post) -> Unit
) : ListAdapter<Post, PostDetailAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return
        val context = holder.itemView.context

        with(holder.binding) {
            val imageViews = listOf(
                imageProduct1, imageProduct2, imageProduct3, imageProduct4, imageProduct5,
                imageProduct6, imageProduct7, imageProduct8, imageProduct9, imageProduct10
            )
            val images = listOf(
                post.postimage, post.postimage2, post.postimage3, post.postimage4, post.postimage5,
                post.postimage6, post.postimage7, post.postimage8, post.postimage9, post.postimage10
            )

            imageProduct.loadImage(false, post.postimage)
            
            imageViews.forEachIndexed { index, imageView ->
                val imageUrl = images[index]
                imageView.loadImage(false, imageUrl)
                imageView.visibility = if (imageUrl == DATA.EMPTY && index > 0) View.GONE else View.VISIBLE
                imageView.setOnClickListener { imageProduct.loadImage(false, imageUrl) }
            }

            val hasMoreImages = images.drop(1).any { it != DATA.EMPTY }
            scrollImage.visibility = if (hasMoreImages) View.VISIBLE else View.GONE

            productName.apply {
                visibility = if (post.name == DATA.EMPTY) View.GONE else View.VISIBLE
                text = post.name
            }
            priceProduct.apply {
                visibility = if (post.price == DATA.EMPTY) View.GONE else View.VISIBLE
                text = MessageFormat.format("{0} SYP", post.price)
            }
            
            linearIndications.visibility = if (post.indications == DATA.EMPTY) View.GONE else View.VISIBLE
            linearIndications2.visibility = if (post.indications == DATA.EMPTY) View.GONE else View.VISIBLE
            textIndications.visibility = if (post.indications == DATA.EMPTY) View.GONE else View.VISIBLE
            indications.apply {
                visibility = if (post.indications == DATA.EMPTY) View.GONE else View.VISIBLE
                text = post.indications
            }

            linearHowToUse.visibility = if (post.use == DATA.EMPTY) View.GONE else View.VISIBLE
            linearHowToUse2.visibility = if (post.use == DATA.EMPTY) View.GONE else View.VISIBLE
            textHowToUse.visibility = if (post.use == DATA.EMPTY) View.GONE else View.VISIBLE
            howToUse.apply {
                visibility = if (post.use == DATA.EMPTY) View.GONE else View.VISIBLE
                text = post.use
            }

            // Bind status from model
            like.setImageResource(if (post.isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
            like.tag = if (post.isLiked) "liked" else "like"
            
            save.setImageResource(if (post.isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
            save.tag = if (post.isSaved) "saved" else "save"
            
            likeNumber.text = MessageFormat.format("{0}", post.nrLikes)

            like.setOnClickListener { onLikeClick(post) }
            save.setOnClickListener { onSaveClick(post) }
        }
    }

    class ViewHolder(val binding: ItemPostDetailBinding) : RecyclerView.ViewHolder(binding.root)

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.postid == newItem.postid
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}