package com.flatcode.beautytouch.ui.reward

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.ui.profile.UserViewModel
import com.flatcode.beautytouch.ui.profile.LeaderboardActivity
import com.flatcode.beautytouch.ui.profile.LeaderboardOldActivity
import com.flatcode.beautytouch.databinding.ActivityRewardBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class RewardActivity : AppCompatActivity() {

    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var binding: ActivityRewardBinding? = null

    private val viewModel: UserViewModel by viewModels()

    var mRewardedAd: RewardedAd? = null
    private var currentYear = ""
    private var currentSession = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        binding!!.toolbar.nameSpace.setText(R.string.earn_points)
        binding!!.leaderboardCard.setOnClickListener {
            context.openActivity<LeaderboardActivity>()
        }
        binding!!.leaderboardCardOld.setOnClickListener {
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
                    currentYear = tools.year!!
                    currentSession = tools.sessionNumber!!
                    val oldYear = tools.oldYear
                    val oldSession = tools.oldSessionNumber
                    if (currentSession != oldSession || currentYear != oldYear) binding!!.leaderboardCardOld.visibility =
                        View.VISIBLE else binding!!.leaderboardCardOld.visibility = View.GONE
                    binding!!.sessionInfo.text = MessageFormat.format("{0} | {1}", currentYear, currentSession)
                    binding!!.sessionInfoOld.text =
                        MessageFormat.format("{0} | {1}", oldYear, oldSession)
                    viewModel.loadPoints(currentYear, currentSession)
                    binding!!.rewardCard.setOnClickListener {
                        loadAndShowRewardedAd()
                        Toast.makeText(
                            context, "The ad is loaded, click again if it does not appear",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.points.collect { resource ->
                if (resource is Resource.Success) {
                    binding!!.myPoints.text = MessageFormat.format("My Points : {0}", resource.data)
                }
            }
        }
    }

    private fun loadRewardedAd() {
        RewardedAd.load(
            context, resources.getString(R.string.admob_reward), AdRequest.Builder().build(),
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
        if (mRewardedAd != null) {
            mRewardedAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
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
            mRewardedAd!!.show(activity!!) { rewardItem: RewardItem? -> }
        }
    }

    private fun loadAndShowRewardedAd() {
        val dialog = ProgressDialog(context)
        dialog.setTitle("Please wait")
        dialog.setMessage("Loading Rewarded Ad")
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        RewardedAd.load(
            context, resources.getString(R.string.admob_reward), AdRequest.Builder().build(),
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