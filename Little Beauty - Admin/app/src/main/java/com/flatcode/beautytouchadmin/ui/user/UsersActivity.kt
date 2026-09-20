package com.flatcode.beautytouchadmin.ui.user

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
import com.flatcode.beautytouchadmin.databinding.ActivityUsersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UsersActivity : AppCompatActivity() {

    private var binding: ActivityUsersBinding? = null
    private val context: Context = this@UsersActivity
    private var adapter: UsersAdapter? = null
    private val viewModel: UsersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.users)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        adapter = UsersAdapter(context)
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.users.collect { users ->
                    binding!!.bar.visibility = View.GONE
                    if (users.isNotEmpty()) {
                        binding!!.recyclerView.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                        adapter?.submitList(users)
                    } else {
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.usersCount.collect { count ->
                    val usersText = getString(R.string.users)
                    binding!!.toolbar.nameSpace.text = "$usersText ( $count )"
                }
            }
        }
    }
}
