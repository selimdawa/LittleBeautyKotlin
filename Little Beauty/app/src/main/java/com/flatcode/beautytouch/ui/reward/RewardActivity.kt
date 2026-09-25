package com.flatcode.beautytouch.ui.reward

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityRewardBinding
import com.flatcode.beautytouch.ui.profile.LeaderboardActivity
import com.flatcode.beautytouch.ui.profile.LeaderboardOldActivity
import com.flatcode.beautytouch.ui.profile.UserViewModel
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.LoadingDialog
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RewardActivity : BaseActivity() {

    private val context: Context = this
    private lateinit var binding: ActivityRewardBinding

    private val viewModel: UserViewModel by viewModels()
    private val dialog by lazy { LoadingDialog(this) }

    private var mRewardedAd: RewardedAd? = null
    private var currentYear = ""
    private var currentSession = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.earn_points)
        binding.leaderboardCard.setOnClickListener {
            context.openActivity<LeaderboardActivity>()
        }
        binding.leaderboardCardOld.setOnClickListener {
            context.openActivity<LeaderboardOldActivity>()
        }
        MobileAds.initialize(context) { }
        loadRewardedAd()

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.appTools.collect { resource ->
                if (resource is Resource.Success) {
                    val tools = resource.data
                    currentYear = tools.year.orEmpty()
                    currentSession = tools.sessionNumber.orEmpty()
                    val oldYear = tools.oldYear
                    val oldSession = tools.oldSessionNumber
                    if (currentSession != oldSession || currentYear != oldYear) {
                        binding.leaderboardCardOld.visibility = View.VISIBLE
                    } else {
                        binding.leaderboardCardOld.visibility = View.GONE
                    }
                    binding.sessionInfo.text = getString(R.string.session_format, currentYear, currentSession)
                    binding.sessionInfoOld.text = getString(R.string.session_format, oldYear, oldSession)
                    viewModel.loadPoints(currentYear, currentSession)
                    binding.rewardCard.setOnClickListener {
                        loadAndShowRewardedAd()
                        Toast.makeText(
                            context,
                            "The ad is loaded, click again if it does not appear",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.points.collect { resource ->
                if (resource is Resource.Success) {
                    binding.myPoints.text = getString(R.string.my_points_format, resource.data)
                }
            }
        }
    }

    private fun loadRewardedAd() {
        RewardedAd.load(
            context,
            resources.getString(R.string.admob_reward),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd
                }
            })
    }

    private fun showRewardedAd() {
        mRewardedAd?.let { ad ->
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    mRewardedAd = null
                    viewModel.addRewardPoint(currentYear, currentSession)
                    loadRewardedAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    mRewardedAd = null
                }
            }
            ad.show(this) { _ -> }
        }
    }

    private fun loadAndShowRewardedAd() {
        dialog.show("Loading Rewarded Ad")
        RewardedAd.load(
            context,
            resources.getString(R.string.admob_reward),
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    super.onAdLoaded(rewardedAd)
                    mRewardedAd = rewardedAd
                    dialog.dismiss()
                    showRewardedAd()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    mRewardedAd = null
                    dialog.dismiss()
                }
            })
    }

    override fun onResume() {
        viewModel.loadAppTools()
        super.onResume()
    }
}