package com.flatcode.beautytouch.ui.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.databinding.FragmentShoppingCentersBinding
import com.flatcode.beautytouch.ui.adapter.ShoppingCentersAdapter
import com.flatcode.beautytouch.ui.post.PostViewModel
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.bannerAd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ShoppingCentersFragment : Fragment() {

    private var _binding: FragmentShoppingCentersBinding? = null
    private val binding get() = _binding!!
    private var adapter: ShoppingCentersAdapter? = null
    private val publisher = DATA.PUBLISHER_NAME
    private val appName = DATA.APP_NAME

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingCentersBinding.inflate(inflater, container, false)

        binding.adView.bannerAd(context, DATA.BANNER_SHOPPING_CENTRES)

        adapter = ShoppingCentersAdapter(
            onItemClick = { _ ->
                // Handle item click if needed
            })
        binding.recyclerView.adapter = adapter

        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.shoppingCenters.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        binding.bar.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyText.visibility = View.GONE
                    }

                    is Resource.Success -> {
                        val items = resource.data.reversed()
                        binding.bar.visibility = View.GONE
                        if (items.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                        adapter?.submitList(items)
                    }

                    is Resource.Error -> {
                        binding.bar.visibility = View.GONE
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        viewModel.loadShoppingCenters(publisher, appName)
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}