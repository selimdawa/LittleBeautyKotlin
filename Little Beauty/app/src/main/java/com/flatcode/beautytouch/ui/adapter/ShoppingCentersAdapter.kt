package com.flatcode.beautytouch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouch.databinding.ItemShoppingCentersBinding
import com.flatcode.beautytouch.model.ShoppingCenter
import com.flatcode.beautytouch.utils.Glide

class ShoppingCentersAdapter(
    private val onItemClick: (ShoppingCenter) -> Unit
) : ListAdapter<ShoppingCenter, ShoppingCentersAdapter.ViewHolder>(ShoppingCenterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingCentersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val context = holder.itemView.context

        with(holder.binding) {
            imageProduct.Glide(false, context, item.imageurl)
            imageProduct2.Glide(false, context, item.imageurl2)
            name.text = item.name
            location.text = item.location
            numberPhone.text = item.numberPhone
            
            root.setOnClickListener { onItemClick(item) }
        }
    }

    class ViewHolder(val binding: ItemShoppingCentersBinding) : RecyclerView.ViewHolder(binding.root)

    class ShoppingCenterDiffCallback : DiffUtil.ItemCallback<ShoppingCenter>() {
        override fun areItemsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean = 
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean = 
            oldItem == newItem
    }
}