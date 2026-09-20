package com.flatcode.beautytouchadmin.ui.other

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityHotProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HotProductActivity : AppCompatActivity() {

    private var binding: ActivityHotProductBinding? = null
    private val context: Context = this@HotProductActivity
    private var hotpostAdapter: HotProductRemoveAdapter? = null
    private var allpostAdapter: HotProductAddAdapter? = null
    private val viewModel: HotProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHotProductBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.hot_product)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }

        hotpostAdapter = HotProductRemoveAdapter(context, object : HotProductRemoveAdapter.OnItemClickListener {
            override fun onRemoveClick(post: Post) {
                viewModel.removeHotProduct(post.postid!!)
            }
        })
        binding!!.recyclerView.adapter = hotpostAdapter

        allpostAdapter = HotProductAddAdapter(context, object : HotProductAddAdapter.OnItemClickListener {
            override fun onAddClick(post: Post) {
                viewModel.addHotProduct(post.postid!!)
            }
        })
        binding!!.recyclerView2.adapter = allpostAdapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hotPosts.collect { posts ->
                    hotpostAdapter?.submitList(posts)
                    binding!!.progressBar.visibility = View.GONE
                    binding!!.recyclerView.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.otherPosts.collect { posts ->
                    allpostAdapter?.submitList(posts)
                    binding!!.progressBar2.visibility = View.GONE
                    binding!!.recyclerView2.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    result.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
