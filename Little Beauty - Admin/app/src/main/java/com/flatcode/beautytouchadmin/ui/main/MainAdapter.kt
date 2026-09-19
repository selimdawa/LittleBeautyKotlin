package com.flatcode.beautytouchadmin.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.model.Main
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.ui.ads.ADsMeterActivity
import com.flatcode.beautytouchadmin.ui.other.HotProductActivity
import com.flatcode.beautytouchadmin.ui.other.SessionNowInfoActivity
import com.flatcode.beautytouchadmin.ui.other.SessionOldInfoActivity
import com.flatcode.beautytouchadmin.ui.other.SliderShowActivity
import com.flatcode.beautytouchadmin.ui.other.ToolsActivity
import com.flatcode.beautytouchadmin.ui.post.PostAddActivity
import com.flatcode.beautytouchadmin.ui.post.PostsActivity
import com.flatcode.beautytouchadmin.ui.profile.AboutMeActivity
import com.flatcode.beautytouchadmin.ui.shopping.ShoppingCentersActivity
import com.flatcode.beautytouchadmin.ui.shopping.ShoppingCentersAddActivity
import com.flatcode.beautytouchadmin.ui.user.UsersActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ItemMainBinding
import java.text.MessageFormat

class MainAdapter(private val context: Context) :
    ListAdapter<Main, MainAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        val image = model.image
        val number = model.number
        val name = model.title

        if (image != 0) holder.image.setImageResource(image) else holder.image.setImageResource(R.drawable.ic_load)
        if (number != 0) {
            holder.number.visibility = View.VISIBLE
            holder.number.text = MessageFormat.format("{0}{1}", DATA.EMPTY, number)
        } else {
            holder.number.visibility = View.GONE
        }
        holder.name.text = name
        holder.itemView.setOnClickListener {
            when (name) {
                "Users" -> context.openActivity<UsersActivity>()
                "Hottest" -> context.openActivity<HotProductActivity>()
                "My Posts" -> context.openActivity<PostsActivity>()
                "Add Post" -> context.openActivity<PostAddActivity>()
                "Shopping Centers" -> context.openActivity<ShoppingCentersActivity>()
                "Add Shopping Center" -> context.openActivity<ShoppingCentersAddActivity>()
                "Current Session" -> context.openActivity<SessionNowInfoActivity>()
                "Previous Session" -> context.openActivity<SessionOldInfoActivity>()
                "Slider Show" -> context.openActivity<SliderShowActivity>()
                "Ad Monitor" -> context.openActivity<ADsMeterActivity>()
                "About Me" -> context.openActivity<AboutMeActivity>()
                "Tools" -> context.openActivity<ToolsActivity>()
            }
        }
    }

    class ViewHolder(binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.name
        val number: TextView = binding.number
        val image: ImageView = binding.image
        val item: LinearLayout = binding.item
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Main>() {
        override fun areItemsTheSame(oldItem: Main, newItem: Main): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Main, newItem: Main): Boolean {
            return oldItem == newItem
        }
    }
}
