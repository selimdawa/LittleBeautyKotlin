package com.flatcode.beautytouch.ui.adapter

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
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ItemPostDetailBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class PostDetailAdapter(private val mContext: Context) :
    ListAdapter<Post, PostDetailAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostDetailBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return

        with(holder) {
            image_product.Glide(false, mContext, post.postimage)
            image_product_1.Glide(false, mContext, post.postimage)
            image_product_2.Glide(false, mContext, post.postimage2)
            image_product_3.Glide(false, mContext, post.postimage3)
            image_product_4.Glide(false, mContext, post.postimage4)
            image_product_5.Glide(false, mContext, post.postimage5)
            image_product_6.Glide(false, mContext, post.postimage6)
            image_product_7.Glide(false, mContext, post.postimage7)
            image_product_8.Glide(false, mContext, post.postimage8)
            image_product_9.Glide(false, mContext, post.postimage9)
            image_product_10.Glide(false, mContext, post.postimage10)

            image_product_2.visibility = if (post.postimage2 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_3.visibility = if (post.postimage3 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_4.visibility = if (post.postimage4 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_5.visibility = if (post.postimage5 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_6.visibility = if (post.postimage6 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_7.visibility = if (post.postimage7 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_8.visibility = if (post.postimage8 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_9.visibility = if (post.postimage9 == DATA.EMPTY) View.GONE else View.VISIBLE
            image_product_10.visibility = if (post.postimage10 == DATA.EMPTY) View.GONE else View.VISIBLE

            val hasMoreImages = post.postimage2 != DATA.EMPTY || post.postimage3 != DATA.EMPTY ||
                    post.postimage4 != DATA.EMPTY || post.postimage5 != DATA.EMPTY ||
                    post.postimage6 != DATA.EMPTY || post.postimage7 != DATA.EMPTY ||
                    post.postimage8 != DATA.EMPTY || post.postimage9 != DATA.EMPTY ||
                    post.postimage10 != DATA.EMPTY

            scroll_image.visibility = if (hasMoreImages) View.VISIBLE else View.GONE

            if (post.name == DATA.EMPTY) {
                product_name.visibility = View.GONE
            } else {
                product_name.visibility = View.VISIBLE
                product_name.text = post.name
            }
            if (post.price == DATA.EMPTY) {
                price_product.visibility = View.GONE
            } else {
                price_product.visibility = View.VISIBLE
                price_product.text = MessageFormat.format("{0} SYP", post.price)
            }
            
            if (post.indications == DATA.EMPTY) {
                linear_indications.visibility = View.GONE
                linear_indications2.visibility = View.GONE
            } else {
                linear_indications.visibility = View.VISIBLE
                text_indications.visibility = View.VISIBLE
                linear_indications2.visibility = View.VISIBLE
                indications.visibility = View.VISIBLE
                indications.text = post.indications
            }
            if (post.use == DATA.EMPTY) {
                linear_how_to_use.visibility = View.GONE
                linear_how_to_use2.visibility = View.GONE
            } else {
                linear_how_to_use.visibility = View.VISIBLE
                text_how_to_use.visibility = View.VISIBLE
                linear_how_to_use2.visibility = View.VISIBLE
                how_to_use.visibility = View.VISIBLE
                how_to_use.text = post.use
            }

            isLiked(post.postid, like)
            isSaved(post.postid, save)
            nrLikes(like_number, post.postid)

            save.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().reference.child(DATA.SAVES)
                    .child(DATA.FirebaseUserUid).child(post.postid)
                if (save.tag == "save") ref.setValue(true) else ref.removeValue()
            }
            like.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().reference.child(DATA.LIKES)
                    .child(post.postid).child(DATA.FirebaseUserUid)
                if (like.tag == "like") ref.setValue(true) else ref.removeValue()
            }
            
            image_product_1.setOnClickListener { image_product.Glide(false, mContext, post.postimage) }
            image_product_2.setOnClickListener { image_product.Glide(false, mContext, post.postimage2) }
            image_product_3.setOnClickListener { image_product.Glide(false, mContext, post.postimage3) }
            image_product_4.setOnClickListener { image_product.Glide(false, mContext, post.postimage4) }
            image_product_5.setOnClickListener { image_product.Glide(false, mContext, post.postimage5) }
            image_product_6.setOnClickListener { image_product.Glide(false, mContext, post.postimage6) }
            image_product_7.setOnClickListener { image_product.Glide(false, mContext, post.postimage7) }
            image_product_8.setOnClickListener { image_product.Glide(false, mContext, post.postimage8) }
            image_product_9.setOnClickListener { image_product.Glide(false, mContext, post.postimage9) }
            image_product_10.setOnClickListener { image_product.Glide(false, mContext, post.postimage10) }
        }
    }

    class ViewHolder(val binding: ItemPostDetailBinding) : RecyclerView.ViewHolder(binding.root) {
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

    private fun isLiked(postId: String, imageView: ImageView) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId)
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

    private fun isSaved(postId: String, imageView: ImageView) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.SAVES).child(DATA.FirebaseUserUid)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.ic_favorites_selected)
                    imageView.tag = "saved"
                } else {
                    imageView.setImageResource(R.drawable.ic_favorites_unselected)
                    imageView.tag = "save"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun nrLikes(likes: TextView, postId: String) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.postid == newItem.postid
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}