package com.flatcode.beautytouch.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.databinding.FragmentHomeBinding
import com.flatcode.beautytouch.ui.adapter.ImageSliderAdapter
import com.flatcode.beautytouch.ui.adapter.PostHotAdapter
import com.flatcode.beautytouch.ui.adapter.PostLinearAdapter
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var hotPostAdapter: PostHotAdapter? = null
    private var allPostAdapter: PostLinearAdapter? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        hotPostAdapter = PostHotAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView.adapter = hotPostAdapter

        allPostAdapter = PostLinearAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView2.adapter = allPostAdapter

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.sliderImages.collect { images ->
                if (images.isNotEmpty()) {
                    binding.imageSlider.setSliderAdapter(ImageSliderAdapter(images))
                }
            }
        }
        lifecycleScope.launch {
            viewModel.hotProducts.collect { posts ->
                binding.progressCircular.visibility = View.GONE
                binding.recyclerView.visibility =
                    if (posts.isNotEmpty()) View.VISIBLE else View.GONE
                hotPostAdapter?.submitList(posts)
            }
        }
        lifecycleScope.launch {
            viewModel.allPosts.collect { posts ->
                binding.progressCircular2.visibility = View.GONE
                binding.recyclerView2.visibility =
                    if (posts.isNotEmpty()) View.VISIBLE else View.GONE
                allPostAdapter?.submitList(posts)
            }
        }
    }

    override fun onResume() {
        viewModel.loadHomeData(publisher, appName)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}