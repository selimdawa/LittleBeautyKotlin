package com.flatcode.beautytouch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ItemProductLinearBinding
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Glide
import com.flatcode.beautytouch.utils.IntentExtra
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class PostHotAdapter(private val mContext: Context?) :
    ListAdapter<Post, PostHotAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductLinearBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position) ?: return

        holder.binding.imageProduct.Glide(false, mContext, post.postimage)

        if (post.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = post.name
        }
        if (post.price == DATA.EMPTY) {
            holder.binding.price.visibility = View.GONE
        } else {
            holder.binding.price.visibility = View.VISIBLE
            holder.binding.price.text = MessageFormat.format("{0} SYP", post.price)
        }

        isLiked(post.postid, holder.binding.like)
        isSaved(post.postid, holder.binding.save)
        nrLikes(holder.binding.likes, post.postid)

        holder.binding.like.setOnClickListener {
            if (holder.binding.like.tag == "like") {
                FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(post.postid)
                    .child(DATA.FirebaseUserUid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(post.postid)
                    .child(DATA.FirebaseUserUid).removeValue()
            }
        }
        holder.binding.save.setOnClickListener {
            if (holder.binding.save.tag == "save") {
                FirebaseDatabase.getInstance().reference.child(DATA.SAVES)
                    .child(DATA.FirebaseUserUid)
                    .child(post.postid).setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference.child(DATA.SAVES)
                    .child(DATA.FirebaseUserUid)
                    .child(post.postid).removeValue()
            }
        }
        holder.binding.card.setOnClickListener {
            mContext.IntentExtra(PostDetailsActivity::class.java, DATA.POST_ID, post.postid)
        }
    }

    class ViewHolder(val binding: ItemProductLinearBinding) : RecyclerView.ViewHolder(binding.root)

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