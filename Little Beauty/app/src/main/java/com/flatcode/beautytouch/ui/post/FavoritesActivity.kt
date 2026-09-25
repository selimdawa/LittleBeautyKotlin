package com.flatcode.beautytouch.ui.post

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityFavoritesBinding
import com.flatcode.beautytouch.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.bannerAd
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FavoritesActivity : BaseActivity() {

    private val context: Context = this@FavoritesActivity
    private lateinit var binding: ActivityFavoritesBinding
    private var adapter: ProductsStaggeredAdapter? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.favorites)
        binding.adView.bannerAd(applicationContext, DATA.BANNER_FAVORITES)

        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoritePosts.collect { resource ->
                Timber.d("Favorite posts collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        binding.bar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyText.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        val posts = resource.data.reversed()
                        binding.bar.visibility = View.GONE
                        if (posts.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                        adapter?.submitList(posts)
                    }

                    is Resource.Error -> {
                        binding.bar.visibility = View.GONE
                        Timber.e("Favorite posts error: ${resource.message}")
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        viewModel.loadFavoritePosts(publisher, appName)
        super.onResume()
    }
}