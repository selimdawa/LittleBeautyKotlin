package com.flatcode.beautytouch.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.extensions.BannerAd
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.databinding.FragmentHairProductsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HairProductsFragment : Fragment() {

    private var binding: FragmentHairProductsBinding? = null
    private var adapter: ProductsStaggeredAdapter? = null
    var publisher = DATA.PUBLISHER_NAME
    var aname = DATA.APP_NAME

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHairProductsBinding.inflate(inflater, container, false)

        binding!!.adView.BannerAd(context, DATA.BANNER_HAIR)

        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) }
        )
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
        return binding!!.root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.postsByCategory.collect { resource ->
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
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        viewModel.loadPostsByCategory(DATA.HAIR_PRODUCTS, publisher, aname)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}