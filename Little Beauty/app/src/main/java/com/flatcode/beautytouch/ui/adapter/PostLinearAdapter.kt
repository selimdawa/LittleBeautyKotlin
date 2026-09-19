package com.flatcode.beautytouch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ItemProductLinearBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import com.flatcode.beautytouch.utils.openActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class PostLinearAdapter(private val mContext: Context?) :
    ListAdapter<Post, PostLinearAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductLinearBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return

        holder.image_product.Glide(false, mContext, post.postimage)
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
            holder.price.text = MessageFormat.format("{0} SYP", post.price)
        }

        isLiked(post.postid, holder.like)
        isSaved(post.postid, holder.save)
        nrLikes(holder.likes, post.postid)

        holder.like.setOnClickListener {
            if (holder.like.tag == "like") {
                FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(post.postid)
                    .child(DATA.FirebaseUserUid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(post.postid)
                    .child(DATA.FirebaseUserUid).removeValue()
            }
        }
        holder.save.setOnClickListener {
            if (holder.save.tag == "save") {
                FirebaseDatabase.getInstance().reference.child(DATA.SAVES)
                    .child(DATA.FirebaseUserUid)
                    .child(post.postid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child(DATA.SAVES)
                    .child(DATA.FirebaseUserUid)
                    .child(post.postid).removeValue()
            }
        }
        holder.card.setOnClickListener {
            mContext?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid)
        }
    }

    class ViewHolder(val binding: ItemProductLinearBinding) : RecyclerView.ViewHolder(binding.root) {
        val card: CardView = binding.card
        val image_product: ImageView = binding.imageProduct
        val like: ImageView = binding.like
        val save: ImageView = binding.save
        val likes: TextView = binding.likes
        val name: TextView = binding.name
        val price: TextView = binding.price
        val color: LinearLayout = binding.color
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
        val reference = FirebaseDatabase.getInstance().reference
            .child(DATA.SAVES).child(DATA.FirebaseUserUid)
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
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postid == newItem.postid
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}