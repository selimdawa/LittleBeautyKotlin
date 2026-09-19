package com.flatcode.beautytouch.ui.post

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.ui.adapter.PostDetailAdapter
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.databinding.ActivityPostDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsActivity : AppCompatActivity() {

    var context: Context = this@PostDetailsActivity
    private var binding: ActivityPostDetailBinding? = null
    private var adapter: PostDetailAdapter? = null
    var postId: String? = null

    private val viewModel: PostDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        val intent = intent
        postId = intent.getStringExtra(DATA.POST_ID)
        binding!!.toolbar.nameSpace.setText(R.string.post_detail)

        adapter = PostDetailAdapter(context)
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postDetails.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        adapter!!.submitList(listOf(resource.data))
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        if (postId != null) {
            viewModel.loadPostDetails(postId!!)
        }
        super.onResume()
    }
}