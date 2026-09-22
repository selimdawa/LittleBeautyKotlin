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
import com.flatcode.beautytouchadmin.model.ADs
import com.flatcode.beautytouchadmin.databinding.ItemInfoAdsBinding

class ADsInfoAdapter(private val context: Context, initialList: MutableList<ADs?>, var isUser: Boolean) :
    ListAdapter<ADs, ADsInfoAdapter.ViewHolder>(DiffCallback), Filterable {

    var list: MutableList<ADs?> = initialList
        set(value) {
            field = value
            submitList(value.filterNotNull())
        }

    var filterList: MutableList<ADs?> = initialList

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
        holder.numberADsLoad.text = "$adsLoadedCount"
        holder.numberADsClick.text = "$adsClickedCount"
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                if (constraint != null && constraint.isNotEmpty()) {
                    val constraintStr = constraint.toString().uppercase()
                    val filter = mutableListOf<ADs?>()
                    for (item in filterList) {
                        if (item?.name?.uppercase()?.contains(constraintStr) == true) {
                            filter.add(item)
                        }
                    }
                    results.count = filter.size
                    results.values = filter
                } else {
                    results.count = filterList.size
                    results.values = filterList
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                @Suppress("UNCHECKED_CAST")
                submitList(results.values as MutableList<ADs>)
            }
        }
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
