package com.flatcode.beautytouch.ui.post

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityFavoritesBinding
import com.flatcode.beautytouch.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.beautytouch.utils.bannerAd
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FavoritesActivity : AppCompatActivity() {

    private val context: Context = this@FavoritesActivity
    private var binding: ActivityFavoritesBinding? = null
    private var adapter: ProductsStaggeredAdapter? = null
    var publisher = DATA.PUBLISHER_NAME
    var aname = DATA.APP_NAME

    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        binding!!.toolbar.nameSpace.setText(R.string.favorites)
        binding!!.adView.bannerAd(applicationContext, DATA.BANNER_FAVORITES)

        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoritePosts.collect { resource ->
                Timber.d("Favorite posts collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        binding!!.bar.visibility = View.VISIBLE
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        val posts = resource.data.reversed()
                        binding!!.bar.visibility = View.GONE
                        if (posts.isNotEmpty()) {
                            binding!!.recyclerView.visibility = View.VISIBLE
                            binding!!.emptyText.visibility = View.GONE
                        } else {
                            binding!!.recyclerView.visibility = View.GONE
                            binding!!.emptyText.visibility = View.VISIBLE
                        }
                        adapter!!.submitList(posts)
                    }

                    is Resource.Error -> {
                        binding!!.bar.visibility = View.GONE
                        Timber.e("Favorite posts error: ${resource.message}")
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        viewModel.loadFavoritePosts(publisher, aname)
        super.onResume()
    }
}