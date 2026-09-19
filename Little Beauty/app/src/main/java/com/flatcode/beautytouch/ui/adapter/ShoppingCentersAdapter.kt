package com.flatcode.beautytouch.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.databinding.ItemShoppingCentersBinding
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.utils.Glide

class ShoppingCentersAdapter(private val mContext: Context?) :
    ListAdapter<ShoppingCenter, ShoppingCentersAdapter.ViewHolder>(ShoppingCenterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingCentersBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.binding.imageProduct.Glide(false, mContext, item.imageurl)
        holder.binding.imageProduct2.Glide(false, mContext, item.imageurl2)
        holder.binding.name.text = item.name
        holder.binding.location.text = item.location
        holder.binding.numberPhone.text = item.numberPhone
    }

    class ViewHolder(val binding: ItemShoppingCentersBinding) : RecyclerView.ViewHolder(binding.root)

    class ShoppingCenterDiffCallback : DiffUtil.ItemCallback<ShoppingCenter>() {
        override fun areItemsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean = 
            oldItem == newItem
    }
}