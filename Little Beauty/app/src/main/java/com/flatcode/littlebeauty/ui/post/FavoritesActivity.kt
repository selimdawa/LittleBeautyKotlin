package com.flatcode.littlebeauty.ui.post

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlebeauty.R
import com.flatcode.littlebeauty.databinding.ActivityFavoritesBinding
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.littlebeauty.utils.BaseActivity
import com.flatcode.littlebeauty.utils.DATA
import com.flatcode.littlebeauty.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesActivity : BaseActivity() {

    private val context: Context = this@FavoritesActivity
    private lateinit var binding: ActivityFavoritesBinding
    private var adapter: ProductsStaggeredAdapter? = null

    private val viewModel: PostViewModel by viewModels()
    private var currentFilter = DATA.ALL
    private var fullFavoritesList = emptyList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.favorites)

        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView.adapter = adapter

        setupCategoryFilter()
        observeViewModel()
    }

    private fun setupCategoryFilter() {
        binding.all.setOnClickListener {
            currentFilter = DATA.ALL
            applyFilter()
        }
        binding.skin.setOnClickListener {
            currentFilter = DATA.SKIN_PRODUCTS
            applyFilter()
        }
        binding.hair.setOnClickListener {
            currentFilter = DATA.HAIR_PRODUCTS
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = when (currentFilter) {
            DATA.SKIN_PRODUCTS -> fullFavoritesList.filter { it.category == DATA.SKIN_PRODUCTS }
            DATA.HAIR_PRODUCTS -> fullFavoritesList.filter { it.category == DATA.HAIR_PRODUCTS }
            else -> fullFavoritesList
        }
        binding.bar.visibility = View.GONE
        if (filtered.isNotEmpty()) {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyText.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.emptyText.visibility = View.VISIBLE
        }
        adapter?.submitList(filtered)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoritePosts.collect { list ->
                fullFavoritesList = list.reversed()
                applyFilter()
            }
        }
    }

    override fun onResume() {
        viewModel.loadFavoritePosts()
        super.onResume()
    }
}