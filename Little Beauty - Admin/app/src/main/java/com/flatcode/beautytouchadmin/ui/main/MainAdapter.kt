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
import com.flatcode.beautytouchadmin.utils.CLASS
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.intent1
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
            val intentClass = when (name) {
                "Users" -> CLASS.USERS
                "Hottest" -> CLASS.HOT_PRODUCTS
                "My Posts" -> CLASS.POSTS
                "Add Post" -> CLASS.POST_ADD
                "Shopping Centers" -> CLASS.SHOPPING_CENTRES
                "Add Shopping Center" -> CLASS.SHOPPING_CENTRES_ADD
                "Current Session" -> CLASS.SESSION_NOW
                "Previous Session" -> CLASS.SESSION_OLD
                "Slider Show" -> CLASS.SLIDER_SHOW
                "Ad Monitor" -> CLASS.ADS_METER
                "About Me" -> CLASS.ABOUT_ME
                "Tools" -> CLASS.TOOLS
                else -> null
            }
            intentClass?.let { context.intent1(it) }
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
