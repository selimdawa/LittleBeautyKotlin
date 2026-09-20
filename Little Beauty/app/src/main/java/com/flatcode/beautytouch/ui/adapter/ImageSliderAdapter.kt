package com.flatcode.beautytouch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import coil3.load
import com.flatcode.beautytouch.databinding.ItemSliderBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(private val images: List<String>) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        viewHolder.binding.imageView.load(images[position])
    }

    override fun getCount(): Int = images.size

    class SliderViewHolder(val binding: ItemSliderBinding) : ViewHolder(binding.root)
}