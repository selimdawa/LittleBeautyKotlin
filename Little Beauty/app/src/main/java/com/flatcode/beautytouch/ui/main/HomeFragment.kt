package com.flatcode.beautytouch.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.ui.adapter.ImageSliderAdapter
import com.flatcode.beautytouch.ui.adapter.PostHotAdapter
import com.flatcode.beautytouch.ui.adapter.PostLinearAdapter
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var hotpostAdapter: PostHotAdapter? = null
    private var allpostAdapter: PostLinearAdapter? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        hotpostAdapter = PostHotAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) }
        )
        binding.recyclerView.adapter = hotpostAdapter

        allpostAdapter = PostLinearAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) }
        )
        binding.recyclerView2.adapter = allpostAdapter

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.sliderImages.collect { resource ->
                Timber.d("Slider images collected: $resource")
                if (resource is Resource.Success) {
                    binding.imageSlider.setSliderAdapter(ImageSliderAdapter(resource.data))
                }
            }
        }
        lifecycleScope.launch {
            viewModel.hotProducts.collect { resource ->
                Timber.d("Hot products collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        hotpostAdapter?.submitList(resource.data)
                        binding.progressCircular.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {
                        binding.progressCircular.visibility = View.GONE
                        Timber.e("Hot products error: ${resource.message}")
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.allPosts.collect { resource ->
                Timber.d("All posts collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        binding.progressCircular2.visibility = View.VISIBLE
                        binding.recyclerView2.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        allpostAdapter?.submitList(resource.data)
                        binding.progressCircular2.visibility = View.GONE
                        binding.recyclerView2.visibility = View.VISIBLE
                    }

                    is Resource.Error -> {
                        binding.progressCircular2.visibility = View.GONE
                        Timber.e("All posts error: ${resource.message}")
                    }

                    else -> {}
                }
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