package com.flatcode.beautytouchadmin.ui.post

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.glide
import com.flatcode.beautytouchadmin.databinding.ItemPostDetailBinding
import java.text.MessageFormat

class PostDetailAdapter(
    private val mContext: Context, 
    initialList: MutableList<Post?>,
    private val listener: OnItemClickListener
) : ListAdapter<Post, PostDetailAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onLikeClick(post: Post)
        fun onSaveClick(post: Post)
    }

    var list: MutableList<Post?> = initialList
        set(value) {
            field = value
            submitList(value.filterNotNull())
        }

    private var isLiked: Boolean = false
    private var isSaved: Boolean = false
    private var likesCount: Long = 0

    init {
        submitList(initialList.filterNotNull())
    }

    fun updateStates(isLiked: Boolean, isSaved: Boolean, likesCount: Long) {
        this.isLiked = isLiked
        this.isSaved = isSaved
        this.likesCount = likesCount
        submitList(list.filterNotNull())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostDetailBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position] ?: return

        holder.image_product.glide(false, post.postimage)
        holder.image_product_1.glide(false, post.postimage)
        holder.image_product_2.glide(false, post.postimage2)
        holder.image_product_3.glide(false, post.postimage3)
        holder.image_product_4.glide(false, post.postimage4)
        holder.image_product_5.glide(false, post.postimage5)
        holder.image_product_6.glide(false, post.postimage6)
        holder.image_product_7.glide(false, post.postimage7)
        holder.image_product_8.glide(false, post.postimage8)
        holder.image_product_9.glide(false, post.postimage9)
        holder.image_product_10.glide(false, post.postimage10)

        // Visibility logic
        holder.image_product_2.visibility = if (post.postimage2 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_3.visibility = if (post.postimage3 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_4.visibility = if (post.postimage4 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_5.visibility = if (post.postimage5 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_6.visibility = if (post.postimage6 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_7.visibility = if (post.postimage7 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_8.visibility = if (post.postimage8 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_9.visibility = if (post.postimage9 == DATA.EMPTY) View.GONE else View.VISIBLE
        holder.image_product_10.visibility = if (post.postimage10 == DATA.EMPTY) View.GONE else View.VISIBLE

        if (post.postimage2 == DATA.EMPTY && post.postimage3 == DATA.EMPTY && post.postimage4 == DATA.EMPTY 
            && post.postimage5 == DATA.EMPTY && post.postimage6 == DATA.EMPTY && post.postimage7 == DATA.EMPTY 
            && post.postimage8 == DATA.EMPTY && post.postimage9 == DATA.EMPTY && post.postimage10 == DATA.EMPTY) {
            holder.scroll_image.visibility = View.GONE
        } else {
            holder.scroll_image.visibility = View.VISIBLE
        }

        if (post.name == DATA.EMPTY) {
            holder.product_name.visibility = View.GONE
        } else {
            holder.product_name.visibility = View.VISIBLE
            holder.product_name.text = post.name
        }
        if (post.price == DATA.EMPTY) {
            holder.price_product.visibility = View.GONE
        } else {
            holder.price_product.visibility = View.VISIBLE
            holder.price_product.text = MessageFormat.format("{0} $", post.price)
        }
        if (post.indications == DATA.EMPTY) {
            holder.linear_indications.visibility = View.GONE
            holder.linear_indications2.visibility = View.GONE
        } else {
            holder.linear_indications.visibility = View.VISIBLE
            holder.text_indications.visibility = View.VISIBLE
            holder.linear_indications2.visibility = View.VISIBLE
            holder.indications.visibility = View.VISIBLE
            holder.indications.text = post.indications
        }
        if (post.use == DATA.EMPTY) {
            holder.linear_how_to_use.visibility = View.GONE
            holder.linear_how_to_use2.visibility = View.GONE
        } else {
            holder.linear_how_to_use.visibility = View.VISIBLE
            holder.text_how_to_use.visibility = View.VISIBLE
            holder.linear_how_to_use2.visibility = View.VISIBLE
            holder.how_to_use.visibility = View.VISIBLE
            holder.how_to_use.text = post.use
        }

        // Like/Save States
        holder.like.setImageResource(if (isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
        holder.save.setImageResource(if (isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
        holder.like_number.text = MessageFormat.format("{0}", likesCount)

        holder.like.setOnClickListener { listener.onLikeClick(post) }
        holder.save.setOnClickListener { listener.onSaveClick(post) }

        // Image selection
        val imageViews = listOf(
            holder.image_product_1, holder.image_product_2, holder.image_product_3, holder.image_product_4, holder.image_product_5,
            holder.image_product_6, holder.image_product_7, holder.image_product_8, holder.image_product_9, holder.image_product_10
        )
        val imageUrls = listOf(
            post.postimage, post.postimage2, post.postimage3, post.postimage4, post.postimage5,
            post.postimage6, post.postimage7, post.postimage8, post.postimage9, post.postimage10
        )

        imageViews.forEachIndexed { index, imageView ->
            buttonClick(imageView, imageUrls[index], holder.image_product)
        }
    }

    private fun buttonClick(view: View, url: String?, mainImage: ImageView) {
        view.setOnClickListener {
            mainImage.glide(false, url)
        }
    }

    class ViewHolder(binding: ItemPostDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        val image_product: ImageView = binding.imageProduct
        val save: ImageView = binding.save
        val like: ImageView = binding.like
        val image_product_1: ImageView = binding.imageProduct1
        val image_product_2: ImageView = binding.imageProduct2
        val image_product_3: ImageView = binding.imageProduct3
        val image_product_4: ImageView = binding.imageProduct4
        val image_product_5: ImageView = binding.imageProduct5
        val image_product_6: ImageView = binding.imageProduct6
        val image_product_7: ImageView = binding.imageProduct7
        val image_product_8: ImageView = binding.imageProduct8
        val image_product_9: ImageView = binding.imageProduct9
        val image_product_10: ImageView = binding.imageProduct10
        val product_name: TextView = binding.productName
        val price_product: TextView = binding.priceProduct
        val like_number: TextView = binding.likeNumber
        val text_indications: TextView = binding.textIndications
        val indications: TextView = binding.indications
        val text_how_to_use: TextView = binding.textHowToUse
        val how_to_use: TextView = binding.howToUse
        val linear_indications: LinearLayout = binding.linearIndications
        val linear_indications2: LinearLayout = binding.linearIndications2
        val linear_how_to_use: LinearLayout = binding.linearHowToUse
        val linear_how_to_use2: LinearLayout = binding.linearHowToUse2
        val scroll_image: HorizontalScrollView = binding.scrollImage
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
