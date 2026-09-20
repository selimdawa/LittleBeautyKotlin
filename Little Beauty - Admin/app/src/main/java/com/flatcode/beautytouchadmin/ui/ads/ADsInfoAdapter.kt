package com.flatcode.beautytouchadmin.ui.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.beautytouchadmin.filter.ADsInfoFilter
import com.flatcode.beautytouchadmin.model.ADs
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.databinding.ItemInfoAdsBinding
import java.text.MessageFormat

class ADsInfoAdapter(private val context: Context, initialList: MutableList<ADs?>, var isUser: Boolean) :
    ListAdapter<ADs, ADsInfoAdapter.ViewHolder>(DiffCallback), Filterable {

    var list: MutableList<ADs?> = initialList
        set(value) {
            field = value
            submitList(value.filterNotNull())
        }

    var filterList: MutableList<ADs?> = initialList
    private var filter: ADsInfoFilter? = null

    init {
        submitList(initialList.filterNotNull())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInfoAdsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position] ?: return

        val name = item.name
        val adsLoadedCount = item.adsLoadedCount
        val adsClickedCount = item.adsClickedCount
        holder.name.text = name
        holder.numberADsLoad.text = MessageFormat.format("{0}{1}", DATA.EMPTY, adsLoadedCount)
        holder.numberADsClick.text = MessageFormat.format("{0}{1}", DATA.EMPTY, adsClickedCount)
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = ADsInfoFilter(filterList, this)
        }
        return filter!!
    }

    class ViewHolder(binding: ItemInfoAdsBinding) : RecyclerView.ViewHolder(binding.root) {
        val numberADsClick: TextView = binding.numberADsClick
        val numberADsLoad: TextView = binding.numberADsLoad
        val name: TextView = binding.name
        val item: LinearLayout = binding.item
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ADs>() {
        override fun areItemsTheSame(oldItem: ADs, newItem: ADs): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ADs, newItem: ADs): Boolean {
            return oldItem == newItem
        }
    }
}
