package com.flatcode.beautytouchadmin.ui.other

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.utils.CLASS
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.glide
import com.flatcode.beautytouchadmin.utils.intentExtra
import com.flatcode.beautytouchadmin.databinding.ItemProductRemoveBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class HotProductRemoveAdapter(
    private val mContext: Context, 
    var list: MutableList<Post?>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<HotProductRemoveAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onRemoveClick(post: Post)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductRemoveBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = list[position] ?: return
        val id = post.postid

        holder.image_product.glide(false, post.postimage)
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
            holder.price.text = MessageFormat.format("{0} $", post.price)
        }

        nrLikes(holder.likes, id)
        holder.remove.setOnClickListener { listener.onRemoveClick(post) }
        holder.card.setOnClickListener {
            mContext.intentExtra(CLASS.POST_DETAILS, DATA.POST_ID, id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(binding: ItemProductRemoveBinding) : RecyclerView.ViewHolder(binding.root) {
        val card: MaterialCardView = binding.card
        val image_product: ImageView = binding.imageProduct
        val likes: TextView = binding.likes
        val name: TextView = binding.name
        val price: TextView = binding.price
        val remove: ImageButton = binding.remove
    }

    private fun nrLikes(likes: TextView, postId: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child(DATA.LIKES).child(postId!!)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likes.text = MessageFormat.format("{0}", dataSnapshot.childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}
