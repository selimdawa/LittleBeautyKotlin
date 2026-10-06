package com.flatcode.littlebeauty.ui.profile

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlebeauty.databinding.ActivityLeaderboardBinding
import com.flatcode.littlebeauty.model.Post
import com.flatcode.littlebeauty.ui.adapter.LeaderboardAdapter
import com.flatcode.littlebeauty.ui.post.PostDetailsActivity
import com.flatcode.littlebeauty.utils.BaseActivity
import com.flatcode.littlebeauty.utils.DATA
import com.flatcode.littlebeauty.utils.loadImage
import com.flatcode.littlebeauty.utils.openActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LeaderboardActivity : BaseActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private val context: Context = this
    private var adapter: LeaderboardAdapter? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = LeaderboardAdapter(
            onItemClick = { _ ->
                // Handle item click if needed
            })
        binding.recyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.appTools.collect { tools ->
                tools?.let {
                    binding.imageSession.loadImage(false, it.imageSession)
                    binding.imageLogo.loadImage(false, it.imageLogo)
                    binding.sessionNumber.text = it.session
                    val key = "${it.year}_${it.sessionNumber}"
                    viewModel.loadLeaderboard(key)
                    viewModel.loadRewards()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.leaderboard.collect { list ->
                adapter?.submitList(list)
                adapter?.filterList = list
            }
        }
        lifecycleScope.launch {
            viewModel.rewards.collect { reward ->
                reward?.let {
                    it.reward?.let { id -> readReward(id, binding.reward) }
                    it.reward2?.let { id -> readReward(id, binding.reward2) }
                    it.reward3?.let { id -> readReward(id, binding.reward3) }
                    it.reward4?.let { id -> readReward(id, binding.reward4) }
                    it.reward5?.let { id -> readReward(id, binding.reward5) }
                    it.reward6?.let { id -> readReward(id, binding.reward6) }
                }
            }
        }
    }

    private fun readReward(rewardId: String, rewardImage: ImageView) {
        if (rewardId != DATA.EMPTY) {
            val reference = FirebaseDatabase.getInstance().getReference(DATA.POSTS).child(rewardId)
            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val post = dataSnapshot.getValue(Post::class.java)
                    if (post?.postid == rewardId) {
                        rewardImage.loadImage(false, post.postimage)
                        rewardImage.setOnClickListener {
                            context.openActivity<PostDetailsActivity>(DATA.POST_ID to rewardId)
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