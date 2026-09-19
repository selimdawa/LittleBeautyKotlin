package com.flatcode.beautytouch.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.ui.adapter.LeaderboardOldAdapter
import com.flatcode.beautytouch.model.Post
import com.flatcode.beautytouch.model.User
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.extensions.Glide
import com.flatcode.beautytouch.utils.extensions.IntentExtra
import com.flatcode.beautytouch.ui.post.PostDetailsActivity
import com.flatcode.beautytouch.databinding.ActivityLeaderboardBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LeaderboardOldActivity : AppCompatActivity() {

    private var binding: ActivityLeaderboardBinding? = null
    var context: Context = this@LeaderboardOldActivity
    private var adapter: LeaderboardOldAdapter? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        adapter = LeaderboardOldAdapter(context)
        binding!!.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.appTools.collect { resource ->
                Timber.d("App tools collected: $resource")
                if (resource is Resource.Success) {
                    val tools = resource.data
                    binding!!.imageSession.Glide(false, context, tools.oldImageSession)
                    binding!!.imageLogo.Glide(false, context, tools.oldImageLogo)
                    binding!!.sessionNumber.text = tools.oldSession
                    val key = tools.oldYear + "_" + tools.oldSessionNumber
                    viewModel.loadLeaderboard(key)
                    viewModel.loadRewards()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.leaderboard.collect { resource ->
                Timber.d("Leaderboard collected: $resource")
                if (resource is Resource.Success) {
                    adapter!!.submitList(resource.data)
                    adapter!!.filterList = ArrayList(resource.data)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.rewards.collect { resource ->
                Timber.d("Rewards collected: $resource")
                if (resource is Resource.Success) {
                    val reward = resource.data
                    reward.reward?.let { ReadReward(it, binding!!.reward) }
                    reward.reward2?.let { ReadReward(it, binding!!.reward2) }
                    reward.reward3?.let { ReadReward(it, binding!!.reward3) }
                    reward.reward4?.let { ReadReward(it, binding!!.reward4) }
                    reward.reward5?.let { ReadReward(it, binding!!.reward5) }
                    reward.reward6?.let { ReadReward(it, binding!!.reward6) }
                }
            }
        }
    }

    private fun ReadReward(R: String, Reward: ImageView) {
        if (R != DATA.EMPTY) {
            val reference = FirebaseDatabase.getInstance().getReference(DATA.POSTS).child(R)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val post = dataSnapshot.getValue(Post::class.java)
                    if (post?.postid == R) {
                        Reward.Glide(false, context, post.postimage)
                        Reward.setOnClickListener {
                            context.IntentExtra(PostDetailsActivity::class.java, DATA.POST_ID, R)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    override fun onResume() {
        viewModel.loadAppTools()
        super.onResume()
    }
}