package com.flatcode.beautytouchadmin.ui.other

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.ui.user.LeaderboardOldAdapter
import com.flatcode.beautytouchadmin.model.User
import com.flatcode.beautytouchadmin.databinding.ActivitySessionOldInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionOldInfoActivity : AppCompatActivity() {

    private var binding: ActivitySessionOldInfoBinding? = null
    private val context: Context = this@SessionOldInfoActivity
    private val list = mutableListOf<User?>()
    private var adapter: LeaderboardOldAdapter? = null
    private val viewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionOldInfoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.text = "Session Old"
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        observeViewModel()
        viewModel.loadSessionInfo(true)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointsKey.collect { key ->
                    if (key != null) {
                        adapter = LeaderboardOldAdapter(context, list, true, key)
                        binding!!.recyclerView.adapter = adapter
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    list.clear()
                    list.addAll(users)
                    adapter?.list = list
                    adapter?.notifyDataSetChanged()

                    binding!!.bar.visibility = View.GONE
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
        viewModel.loadSessionInfo(true)
    }
}
