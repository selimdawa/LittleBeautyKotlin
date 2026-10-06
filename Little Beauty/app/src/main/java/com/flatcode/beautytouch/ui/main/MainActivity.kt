package com.flatcode.beautytouch.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.flatcode.beautytouch.BuildConfig
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.databinding.ActivityMainBinding
import com.flatcode.beautytouch.ui.post.FavoritesActivity
import com.flatcode.beautytouch.ui.post.PostViewModel
import com.flatcode.beautytouch.ui.profile.ProfileActivity
import com.flatcode.beautytouch.ui.profile.UserViewModel
import com.flatcode.beautytouch.ui.reward.RewardActivity
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val context: Context = this
    private val home = "Home Page"
    private val skinProduct = "Skin Products"
    private val hairProduct = "Hair Products"
    private val shoppingCenter = "Shopping Centers"
    private var bottomNavigation: BubbleBottomNavigation? = null
    private val publisher: String = DATA.PUBLISHER_NAME
    private val appName: String = DATA.APP_NAME

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val toolbar = v.findViewById<View>(R.id.toolbar)
            val bottomNav = v.findViewById<View>(R.id.bottomNavigation)
            val navView = v.findViewById<View>(R.id.nav_view)

            if (toolbar != null || bottomNav != null || navView != null) {
                toolbar?.setPadding(
                    toolbar.paddingLeft, systemBars.top, toolbar.paddingRight, toolbar.paddingBottom
                )
                bottomNav?.setPadding(
                    bottomNav.paddingLeft,
                    bottomNav.paddingTop,
                    bottomNav.paddingRight,
                    systemBars.bottom
                )
                navView?.setPadding(
                    navView.paddingLeft, systemBars.top, navView.paddingRight, systemBars.bottom
                )
            } else {
                v.setPadding(
                    systemBars.left, systemBars.top, systemBars.right, systemBars.bottom
                )
            }
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else if (navController.currentDestination?.id != R.id.homeFragment) {
                    bottomNavigation?.show(2, true)
                    navController.popBackStack(R.id.homeFragment, false)
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
                val tools = userViewModel.appTools.first { it != null }
                tools?.let {
                    showDialogAboutMy(it.imageMe, it.aboutMe)
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
            val navOptions =
                NavOptions.Builder().setLaunchSingleTop(true).setPopUpTo(R.id.homeFragment, false)
                    .build()
            when (item.id) {
                1 -> if (navController.currentDestination?.id != R.id.skinProductsFragment) {
                    navController.navigate(R.id.skinProductsFragment, null, navOptions)
                }

                2 -> if (navController.currentDestination?.id != R.id.homeFragment) {
                    navController.popBackStack(R.id.homeFragment, false)
                }

                3 -> if (navController.currentDestination?.id != R.id.hairProductsFragment) {
                    navController.navigate(R.id.hairProductsFragment, null, navOptions)
                }

                4 -> if (navController.currentDestination?.id != R.id.shoppingCentersFragment) {
                    navController.navigate(R.id.shoppingCentersFragment, null, navOptions)
                }
            }
        }

        bottomNavigation.show(2, false)

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
        binding.version.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)

        observeViewModels()
        userViewModel.loadUserInfo()
        postViewModel.loadSkinProducts(publisher, appName)
        postViewModel.loadHairProducts(publisher, appName)
        postViewModel.loadShoppingCenters(publisher, appName)
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

        lifecycleScope.launch {
            postViewModel.skinProducts.collect { list ->
                bottomNavigation?.setCount(1, list.size.toString())
                binding.numberProductSkin.text = list.size.toString()
            }
        }

        lifecycleScope.launch {
            postViewModel.hairProducts.collect { list ->
                bottomNavigation?.setCount(3, list.size.toString())
                binding.numberProductHair.text = list.size.toString()
            }
        }

        lifecycleScope.launch {
            postViewModel.shoppingCenters.collect { list ->
                bottomNavigation?.setCount(4, list.size.toString())
                binding.numberShoppingCenters.text = list.size.toString()
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
            Intent.EXTRA_TEXT, getString(R.string.share_app_text, BuildConfig.APPLICATION_ID)
        )
        startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
    }

    companion object {
        var mInterstitialAd: InterstitialAd? = null
    }
}