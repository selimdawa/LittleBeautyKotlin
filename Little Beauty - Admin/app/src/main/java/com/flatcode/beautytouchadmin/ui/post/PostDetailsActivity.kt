package com.flatcode.beautytouchadmin.ui.post

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.model.Post
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.databinding.ActivityPostDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDetailsActivity : AppCompatActivity() {

    private val context: Context = this@PostDetailsActivity
    private var binding: ActivityPostDetailsBinding? = null
    private var adapter: PostDetailAdapter? = null
    private var postId: String? = null
    private val viewModel: PostDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        postId = intent.getStringExtra(DATA.POST_ID)

        binding!!.toolbar.nameSpace.setText(R.string.post_detail)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = PostDetailAdapter(context, mutableListOf(), object : PostDetailAdapter.OnItemClickListener {
            override fun onLikeClick(post: Post) {
                viewModel.toggleLike(post.postid)
            }

            override fun onSaveClick(post: Post) {
                viewModel.toggleSave(post.postid)
            }
        })
        binding!!.recyclerView.adapter = adapter

        postId?.let { viewModel.loadPost(it) }
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.post.collect { post ->
                    post?.let {
                        adapter?.list = mutableListOf(it)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    viewModel.isLiked,
                    viewModel.isSaved,
                    viewModel.likesCount
                ) { isLiked, isSaved, likesCount ->
                    Triple(isLiked, isSaved, likesCount)
                }.collect { (isLiked, isSaved, likesCount) ->
                    adapter?.updateStates(isLiked, isSaved, likesCount)
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

    override fun onResume() {
        super.onResume()
        postId?.let { viewModel.loadPost(it) }
    }
}
