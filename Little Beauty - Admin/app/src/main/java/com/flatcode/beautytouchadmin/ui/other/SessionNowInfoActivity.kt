package com.flatcode.beautytouchadmin.ui.other

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivitySessionNowInfoBinding
import com.flatcode.beautytouchadmin.ui.user.LeaderboardAdapter
import com.flatcode.beautytouchadmin.utils.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SessionNowInfoActivity : BaseActivity() {

    private var binding: ActivitySessionNowInfoBinding? = null
    private val context: Context = this@SessionNowInfoActivity
    private var adapter: LeaderboardAdapter? = null
    private val viewModel: SessionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionNowInfoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.session_now)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        observeViewModel()
        viewModel.loadSessionInfo(false)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pointsKey.collect { key ->
                    if (key != null) {
                        adapter = LeaderboardAdapter(context, key)
                        binding!!.recyclerView.adapter = adapter
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    adapter?.submitList(users)

                    binding!!.bar.visibility = View.GONE
                    if (users.isNotEmpty()) {
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
        viewModel.loadSessionInfo(false)
    }
}
