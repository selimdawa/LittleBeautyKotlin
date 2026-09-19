package com.flatcode.beautytouchadmin.ui.ads

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.databinding.ActivityAdsMeterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ADsMeterActivity : AppCompatActivity() {

    private var binding: ActivityAdsMeterBinding? = null
    private val context: Context = this@ADsMeterActivity
    private val list = mutableListOf<User?>()
    private var adapter: ADsUserAdapter? = null
    private var type: String = DATA.AD_LOAD
    private val viewModel: ADsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdsMeterBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.users_ads)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        adapter = ADsUserAdapter(context, list, true)
        binding!!.recyclerView.adapter = adapter

        binding!!.adLoad.setOnClickListener {
            type = DATA.AD_LOAD
            viewModel.fetchUsersWithAds(type)
        }
        binding!!.adClick.setOnClickListener {
            type = DATA.AD_CLICK
            viewModel.fetchUsersWithAds(type)
        }
        binding!!.timestamp.setOnClickListener {
            type = DATA.STARTED
            viewModel.fetchUsersWithAds(type)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    list.clear()
                    list.addAll(users)
                    adapter?.list = list
                    adapter?.notifyDataSetChanged()

                    binding!!.progress.visibility = View.GONE
                    if (list.isNotEmpty()) {
                        binding!!.recyclerView.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                    } else {
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUsersWithAds(type)
    }
}
