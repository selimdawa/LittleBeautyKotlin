package com.flatcode.littlebeauty.ui.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlebeauty.databinding.FragmentHairProductsBinding
import com.flatcode.littlebeauty.ui.adapter.ProductsStaggeredAdapter
import com.flatcode.littlebeauty.utils.DATA
import com.flatcode.littlebeauty.utils.bannerAd
import com.flatcode.littlebeauty.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HairProductsFragment : Fragment() {

    private var _binding: FragmentHairProductsBinding? = null
    private val binding get() = _binding!!
    private var adapter: ProductsStaggeredAdapter? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHairProductsBinding.inflate(inflater, container, false)

        binding.adView.bannerAd(context, DATA.BANNER_HAIR)

        adapter = ProductsStaggeredAdapter(
            onItemClick = { post -> context?.openActivity<PostDetailsActivity>(DATA.POST_ID to post.postid) },
            onLikeClick = { post -> viewModel.toggleLike(post) },
            onSaveClick = { post -> viewModel.toggleSave(post) })
        binding.recyclerView.adapter = adapter

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postsByCategory.collect { list ->
                    _binding?.let { binding ->
                        val posts = list.reversed()
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
                }
            }
        }
    }

    override fun onResume() {
        viewModel.loadPostsByCategory(DATA.HAIR_PRODUCTS, publisher, appName)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}