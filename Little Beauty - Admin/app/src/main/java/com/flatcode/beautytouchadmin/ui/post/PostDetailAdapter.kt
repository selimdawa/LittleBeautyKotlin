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
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ItemPostDetailBinding
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.utils.loadImage

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

        holder.imageProduct.loadImage(false, post.postimage)
        holder.imageProduct1.loadImage(false, post.postimage)
        holder.imageProduct2.loadImage(false, post.postimage2)
        holder.imageProduct3.loadImage(false, post.postimage3)
        holder.imageProduct4.loadImage(false, post.postimage4)
        holder.imageProduct5.loadImage(false, post.postimage5)
        holder.imageProduct6.loadImage(false, post.postimage6)
        holder.imageProduct7.loadImage(false, post.postimage7)
        holder.imageProduct8.loadImage(false, post.postimage8)
        holder.imageProduct9.loadImage(false, post.postimage9)
        holder.imageProduct10.loadImage(false, post.postimage10)

        // Visibility logic
        holder.imageProduct2.visibility =
            if (post.postimage2.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct3.visibility =
            if (post.postimage3.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct4.visibility =
            if (post.postimage4.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct5.visibility =
            if (post.postimage5.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct6.visibility =
            if (post.postimage6.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct7.visibility =
            if (post.postimage7.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct8.visibility =
            if (post.postimage8.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct9.visibility =
            if (post.postimage9.isNullOrEmpty()) View.GONE else View.VISIBLE
        holder.imageProduct10.visibility =
            if (post.postimage10.isNullOrEmpty()) View.GONE else View.VISIBLE

        if (post.postimage2.isNullOrEmpty() && post.postimage3.isNullOrEmpty() && post.postimage4.isNullOrEmpty() && post.postimage5.isNullOrEmpty() && post.postimage6.isNullOrEmpty() && post.postimage7.isNullOrEmpty() && post.postimage8.isNullOrEmpty() && post.postimage9.isNullOrEmpty() && post.postimage10.isNullOrEmpty()) {
            holder.scrollImage.visibility = View.GONE
        } else {
            holder.scrollImage.visibility = View.VISIBLE
        }

        if (post.name.isNullOrEmpty()) {
            holder.productName.visibility = View.GONE
        } else {
            holder.productName.visibility = View.VISIBLE
            holder.productName.text = post.name
        }
        if (post.price.isNullOrEmpty()) {
            holder.priceProduct.visibility = View.GONE
        } else {
            holder.priceProduct.visibility = View.VISIBLE
            holder.priceProduct.text = mContext.getString(R.string.price_format, post.price)
        }
        if (post.indications.isNullOrEmpty()) {
            holder.linearIndications.visibility = View.GONE
            holder.linearIndications2.visibility = View.GONE
        } else {
            holder.linearIndications.visibility = View.VISIBLE
            holder.textIndications.visibility = View.VISIBLE
            holder.linearIndications2.visibility = View.VISIBLE
            holder.indications.visibility = View.VISIBLE
            holder.indications.text = post.indications
        }
        if (post.use.isNullOrEmpty()) {
            holder.linearHowToUse.visibility = View.GONE
            holder.linearHowToUse2.visibility = View.GONE
        } else {
            holder.linearHowToUse.visibility = View.VISIBLE
            holder.textHowToUse.visibility = View.VISIBLE
            holder.linearHowToUse2.visibility = View.VISIBLE
            holder.howToUse.visibility = View.VISIBLE
            holder.howToUse.text = post.use
        }

        // Like/Save States
        holder.like.setImageResource(if (isLiked) R.drawable.ic_heart_selected else R.drawable.ic_heart_unselected)
        holder.save.setImageResource(if (isSaved) R.drawable.ic_favorites_selected else R.drawable.ic_favorites_unselected)
        holder.likeNumber.text = likesCount.toString()

        holder.like.setOnClickListener { listener.onLikeClick(post) }
        holder.save.setOnClickListener { listener.onSaveClick(post) }

        // Image selection
        val imageViews = listOf(
            holder.imageProduct1,
            holder.imageProduct2,
            holder.imageProduct3,
            holder.imageProduct4,
            holder.imageProduct5,
            holder.imageProduct6,
            holder.imageProduct7,
            holder.imageProduct8,
            holder.imageProduct9,
            holder.imageProduct10
        )
        val imageUrls = listOf(
            post.postimage,
            post.postimage2,
            post.postimage3,
            post.postimage4,
            post.postimage5,
            post.postimage6,
            post.postimage7,
            post.postimage8,
            post.postimage9,
            post.postimage10
        )

        imageViews.forEachIndexed { index, imageView ->
            buttonClick(imageView, imageUrls[index], holder.imageProduct)
        }
    }

    private fun buttonClick(view: View, url: String?, mainImage: ImageView) {
        view.setOnClickListener {
            mainImage.loadImage(false, url)
        }
    }

    class ViewHolder(binding: ItemPostDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageProduct: ImageView = binding.imageProduct
        val save: ImageView = binding.save
        val like: ImageView = binding.like
        val imageProduct1: ImageView = binding.imageProduct1
        val imageProduct2: ImageView = binding.imageProduct2
        val imageProduct3: ImageView = binding.imageProduct3
        val imageProduct4: ImageView = binding.imageProduct4
        val imageProduct5: ImageView = binding.imageProduct5
        val imageProduct6: ImageView = binding.imageProduct6
        val imageProduct7: ImageView = binding.imageProduct7
        val imageProduct8: ImageView = binding.imageProduct8
        val imageProduct9: ImageView = binding.imageProduct9
        val imageProduct10: ImageView = binding.imageProduct10
        val productName: TextView = binding.productName
        val priceProduct: TextView = binding.priceProduct
        val likeNumber: TextView = binding.likeNumber
        val textIndications: TextView = binding.textIndications
        val indications: TextView = binding.indications
        val textHowToUse: TextView = binding.textHowToUse
        val howToUse: TextView = binding.howToUse
        val linearIndications: LinearLayout = binding.linearIndications
        val linearIndications2: LinearLayout = binding.linearIndications2
        val linearHowToUse: LinearLayout = binding.linearHowToUse
        val linearHowToUse2: LinearLayout = binding.linearHowToUse2
        val scrollImage: HorizontalScrollView = binding.scrollImage
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
