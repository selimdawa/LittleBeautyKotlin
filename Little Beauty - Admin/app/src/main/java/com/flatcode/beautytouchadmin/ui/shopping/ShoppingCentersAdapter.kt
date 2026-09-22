package com.flatcode.beautytouchadmin.ui.shopping

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
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ItemShoppingCenterBinding
import com.flatcode.beautytouchadmin.model.ShoppingCenter
import com.flatcode.beautytouchadmin.utils.loadImage

class ShoppingCentersAdapter(
    private val mContext: Context,
    private val listener: OnItemClickListener
) : ListAdapter<ShoppingCenter, ShoppingCentersAdapter.ViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onMoreClick(item: ShoppingCenter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemShoppingCenterBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shoppingCenter = getItem(position) ?: return

        holder.imageProduct.loadImage(false, shoppingCenter.imageurl)
        holder.imageProduct2.loadImage(false, shoppingCenter.imageurl2)
        if (shoppingCenter.name.isNullOrEmpty()) {
            holder.linearName.visibility = View.GONE
        } else {
            holder.linearName.visibility = View.VISIBLE
            holder.name.text = shoppingCenter.name
        }
        if (shoppingCenter.location.isNullOrEmpty() && shoppingCenter.location2.isNullOrEmpty()) {
            holder.linearLocation.visibility = View.GONE
            holder.view.visibility = View.GONE
        } else {
            holder.linearLocation.visibility = View.VISIBLE
            holder.view.visibility = View.VISIBLE
            holder.location.text = mContext.getString(
                R.string.location_format,
                shoppingCenter.location,
                shoppingCenter.location2,
                shoppingCenter.location3
            )
        }
        if (shoppingCenter.numberPhone.isNullOrEmpty()) {
            holder.linearNumberPhone.visibility = View.GONE
            holder.view2.visibility = View.GONE
        } else {
            holder.linearNumberPhone.visibility = View.VISIBLE
            holder.view2.visibility = View.VISIBLE
            holder.numberPhone.text = shoppingCenter.numberPhone
        }
        holder.more.setOnClickListener { listener.onMoreClick(shoppingCenter) }
    }

    class ViewHolder(binding: ItemShoppingCenterBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageProduct: ImageView = binding.imageProduct
        val imageProduct2: ImageView = binding.imageProduct2
        val more: ImageView = binding.more
        val name: TextView = binding.name
        val location: TextView = binding.location
        val numberPhone: TextView = binding.numberPhone
        val linearName: LinearLayout = binding.linearName
        val linearLocation: LinearLayout = binding.linearLocation
        val linearNumberPhone: LinearLayout = binding.linearNumberPhone
        val view: View = binding.view
        val view2: View = binding.view2
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ShoppingCenter>() {
        override fun areItemsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShoppingCenter, newItem: ShoppingCenter): Boolean {
            return oldItem == newItem
        }
    }
}
