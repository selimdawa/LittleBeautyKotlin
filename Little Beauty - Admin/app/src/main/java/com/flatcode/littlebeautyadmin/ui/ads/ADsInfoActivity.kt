package com.flatcode.littlebeautyadmin.ui.ads

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.flatcode.littlebeautyadmin.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlebeautyadmin.R
import com.flatcode.littlebeautyadmin.databinding.ActivityAdsInfoBinding
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ADsInfoActivity : BaseActivity() {

    private var binding: ActivityAdsInfoBinding? = null
    private val context: Context = this@ADsInfoActivity
    private var adapter: ADsInfoAdapter? = null
    private var profileId: String? = null
    private val viewModel: ADsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdsInfoBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        profileId = intent.getStringExtra(DATA.PROFILE_ID)

        binding!!.toolbar.nameSpace.setText(R.string.info_ads)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = ADsInfoAdapter(context)
        binding!!.recyclerView.adapter = adapter

        profileId?.let {
            viewModel.fetchUserInfo(it)
            viewModel.fetchAds(it, DATA.NAME)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { user ->
                    user?.let {
                        binding!!.username.text = it.username
                        binding!!.profileImage.loadImage(true, it.imageurl)
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ads.collect { ads ->
                    adapter?.submitList(ads)

                    binding!!.progress.visibility = View.GONE
                    if (ads.isNotEmpty()) {
                        binding!!.recyclerView.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                    } else {
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        profileId?.let { viewModel.fetchAds(it, DATA.NAME) }
    }
}
