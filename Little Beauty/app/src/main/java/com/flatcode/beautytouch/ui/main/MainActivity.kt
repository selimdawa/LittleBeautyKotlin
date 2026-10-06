package com.flatcode.beautytouch.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.beautytouch.BuildConfig
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityMainBinding
import com.flatcode.beautytouch.ui.post.FavoritesActivity
import com.flatcode.beautytouch.ui.post.PostViewModel
import com.flatcode.beautytouch.ui.profile.ProfileActivity
import com.flatcode.beautytouch.ui.profile.UserViewModel
import com.flatcode.beautytouch.ui.reward.RewardActivity
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.closeApp
import com.flatcode.beautytouch.utils.dialogAboutApp
import com.flatcode.beautytouch.utils.dialogLogout
import com.flatcode.beautytouch.utils.interstitialAd
import com.flatcode.beautytouch.utils.interstitialShow
import com.flatcode.beautytouch.utils.loadImage
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.utils.showDialogAboutMy
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import io.selimdawa.bubblebottom.Model
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val context: Context = this
    private val home = "Home Page"
    private val skinProduct = "Skin Products"
    private val hairProduct = "Hair Products"
    private val shoppingCenter = "Shopping Centers"
    private val numberProduct = DATA.EMPTY
    private var bottomNavigation: BubbleBottomNavigation? = null
    private val publisher: String = DATA.PUBLISHER_NAME
    private val appName: String = DATA.APP_NAME

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    closeApp()
                }
            }
        })

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.toolbar.image.setOnClickListener { context.openActivity<ProfileActivity>() }
        binding.toolbar.drawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        MobileAds.initialize(this) { }
        interstitialAd()

        binding.myProfile.setOnClickListener {
            context.openActivity<ProfileActivity>()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.favorites.setOnClickListener { context.openActivity<FavoritesActivity>() }
        binding.messenger.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = "https://wa.me/message/E2YOU4NVTIEAD1".toUri()
            startActivity(i)
        }
        binding.reward.setOnClickListener { context.openActivity<RewardActivity>() }
        binding.aboutApp.setOnClickListener { dialogAboutApp() }
        binding.shareApp.setOnClickListener { shareApp() }
        binding.aboutMy.setOnClickListener {
            lifecycleScope.launch {
                userViewModel.appTools.collect { tools ->
                    tools?.let {
                        showDialogAboutMy(it.imageMe, it.aboutMe)
                    }
                }
            }
            userViewModel.loadAppTools()
        }
        binding.logout.setOnClickListener { dialogLogout() }

        val bottomNavigation = binding.bottomNavigation
        this.bottomNavigation = bottomNavigation
        bottomNavigation.add(Model(1, R.drawable.ic_skin))
        bottomNavigation.add(Model(2, R.drawable.ic_home))
        bottomNavigation.add(Model(3, R.drawable.ic_hair))
        bottomNavigation.add(Model(4, R.drawable.ic_shopping_centers))

        bottomNavigation.setOnShowListener { item: Model ->
            when (item.id) {
                1 -> navController.navigate(R.id.skinProductsFragment)
                2 -> navController.navigate(R.id.homeFragment)
                3 -> navController.navigate(R.id.hairProductsFragment)
                4 -> navController.navigate(R.id.shoppingCentersFragment)
            }
        }

        bottomNavigation.setCount(1, numberProduct)
        bottomNavigation.setCount(3, numberProduct)
        bottomNavigation.setCount(4, numberProduct)
        bottomNavigation.show(2, true)

        bottomNavigation.setOnClickMenuListener { item: Model ->
            when (item.id) {
                4 -> {
                    interstitialShow(DATA.INTERSTITIAL_HOME)
                }
            }
        }
        bottomNavigation.setOnReselectListener { item: Model ->
            when (item.id) {
                1 -> Toast.makeText(applicationContext, skinProduct, Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(applicationContext, home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, hairProduct, Toast.LENGTH_SHORT).show()
                4 -> Toast.makeText(applicationContext, shoppingCenter, Toast.LENGTH_SHORT).show()
            }
        }
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.imageDrawer.setOnClickListener { context.openActivity<ProfileActivity>() }

        observeViewModels()
        userViewModel.loadUserInfo()
    }

    private fun observeViewModels() {
        lifecycleScope.launch {
            userViewModel.userInfo.collect { user ->
                user?.let {
                    binding.imageDrawer.loadImage(true, it.imageurl)
                    binding.toolbar.image.loadImage(true, it.imageurl)
                    binding.name.text = it.username
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            " Little Beauty: beauty care application, download it now from Google Play " + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
    }

    companion object {
        var mInterstitialAd: InterstitialAd? = null
    }
}