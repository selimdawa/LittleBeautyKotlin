package com.flatcode.beautytouch.ui.post

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityPostDetailBinding
import com.flatcode.beautytouch.ui.adapter.PostDetailAdapter
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private var adapter: PostDetailAdapter? = null
    private var postId: String? = null

    private val viewModel: PostDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postId = intent.getStringExtra(DATA.POST_ID)

        adapter = PostDetailAdapter(
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postDetails.collect { post ->
                post?.let {
                    adapter?.submitList(listOf(it))
                }
            }
        }
    }

    override fun onResume() {
        postId?.let { viewModel.loadPostDetails(it) }
        super.onResume()
    }
}