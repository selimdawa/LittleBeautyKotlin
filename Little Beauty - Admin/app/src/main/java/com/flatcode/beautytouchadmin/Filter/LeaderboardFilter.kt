package com.flatcode.beautytouchadmin.filter

import android.widget.Filter
import com.flatcode.beautytouchadmin.ui.user.LeaderboardAdapter
import com.flatcode.beautytouchadmin.model.User
import java.util.*

class LeaderboardFilter(var list: MutableList<User?>, var adapter: LeaderboardAdapter) : Filter() {
    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraintStr: CharSequence? = constraint
        val results = FilterResults()
        if (constraintStr != null && constraintStr.isNotEmpty()) {
            constraintStr = constraintStr.toString().uppercase(Locale.getDefault())
            val filter = mutableListOf<User?>()
            for (i in list.indices) {
                if (list[i]!!.username!!.uppercase(Locale.getDefault()).contains(constraintStr)) {
                    filter.add(list[i])
                }
            }
            results.count = filter.size
            results.values = filter
        } else {
            results.count = list.size
            results.values = list
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapter.submitList(results.values as MutableList<User>)
    }
}
