package com.flatcode.littlebeauty.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlebeauty.databinding.FragmentHomeBinding
import com.flatcode.littlebeauty.ui.adapter.ImageSliderAdapter
import com.flatcode.littlebeauty.ui.adapter.PostHotAdapter
import com.flatcode.littlebeauty.ui.adapter.PostLinearAdapter
import com.flatcode.littlebeauty.R
import com.flatcode.littlebeauty.ui.post.PostDetailsActivity
import com.flatcode.littlebeauty.ui.post.ShowMoreActivity
import com.flatcode.littlebeauty.utils.DATA
import com.flatcode.littlebeauty.utils.openActivity
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

        binding.hotProduct.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.HOT_PRODUCT,
                DATA.SHOW_MORE_NAME to getString(R.string.most_hot)
            )
        }

        binding.showMoreProduct.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.ALL,
                DATA.SHOW_MORE_NAME to getString(R.string.show_more)
            )
        }

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sliderImages.collect { images ->
                        _binding?.let { binding ->
                            if (images.isNotEmpty()) {
                                binding.imageSlider.setSliderAdapter(ImageSliderAdapter(images))
                            }
                        }
                    }
                }
                launch {
                    viewModel.hotProducts.collect { posts ->
                        _binding?.let { binding ->
                            binding.bar.visibility = View.GONE
                            binding.recyclerView.visibility =
                                if (posts.isNotEmpty()) View.VISIBLE else View.GONE
                            hotPostAdapter?.submitList(posts)
                        }
                    }
                }
                launch {
                    viewModel.allPosts.collect { posts ->
                        _binding?.let { binding ->
                            binding.bar2.visibility = View.GONE
                            binding.recyclerView2.visibility =
                                if (posts.isNotEmpty()) View.VISIBLE else View.GONE
                            allPostAdapter?.submitList(posts)
                        }
                    }
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