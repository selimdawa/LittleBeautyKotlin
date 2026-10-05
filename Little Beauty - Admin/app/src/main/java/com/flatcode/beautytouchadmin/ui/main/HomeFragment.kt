package com.flatcode.beautytouchadmin.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.FragmentHomeBinding
import com.flatcode.beautytouchadmin.model.Main
import com.flatcode.beautytouchadmin.ui.profile.ProfileActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)
    private val list = mutableListOf<Main>()
    private var adapter: MainAdapter? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.image.setOnClickListener {
            requireContext().openActivity<ProfileActivity>(
                extras = arrayOf(DATA.PROFILE_ID to DATA.FirebaseUserUid)
            )
        }

        adapter = MainAdapter(requireContext())
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    if (!state.isLoading) {
                        updateUI(state)
                    }
                }
            }
        }
    }

    private fun updateUI(state: MainState) {
        binding.toolbar.image.loadImage(true, state.user?.imageurl)

        list.clear()
        list.add(Main(R.drawable.ic_person_white, "Users", state.usersCount))
        list.add(Main(R.drawable.ic_hot, "Hottest", state.hotProductsCount))
        list.add(Main(R.drawable.ic_post, "My Posts", state.postsCount))
        list.add(Main(R.drawable.ic_add, "Add Post", 0))
        list.add(Main(R.drawable.ic_store, "Shopping Centers", state.shoppingCentersCount))
        list.add(Main(R.drawable.ic_add, "Add Shopping Center", 0))
        list.add(Main(R.drawable.ic_rank, "Current Session", 0))
        list.add(Main(R.drawable.ic_rank, "Previous Session", 0))
        list.add(Main(R.drawable.ic_slider, "Slider Show", state.sliderShowCount))
        list.add(Main(R.drawable.ic_ad, "Ad Monitor", 0))
        list.add(Main(R.drawable.ic_my, "About Me", 0))
        list.add(Main(R.drawable.ic_settings, "Tools", 0))

        adapter?.submitList(list.toList())
        binding.progress.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }
}
