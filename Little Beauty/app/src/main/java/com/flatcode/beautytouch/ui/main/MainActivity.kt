package com.flatcode.beautytouch.ui.main

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import coil3.load
import com.flatcode.beautytouch.ui.auth.LoginActivity
import com.flatcode.beautytouch.ui.profile.ProfileActivity
import com.flatcode.beautytouch.ui.profile.UserViewModel
import com.flatcode.beautytouch.ui.post.PostViewModel
import com.flatcode.beautytouch.ui.post.FavoritesActivity
import com.flatcode.beautytouch.ui.reward.RewardActivity
import com.flatcode.beautytouch.BuildConfig
import com.flatcode.beautytouch.R
import com.flatcode.beautytouch.utils.DATA
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.utils.interstitialAd
import com.flatcode.beautytouch.utils.interstitialShow
import com.flatcode.beautytouch.utils.loadImage
import com.flatcode.beautytouch.utils.rateUs
import com.flatcode.beautytouch.databinding.ActivityMainBinding
import com.flatcode.beautytouch.databinding.DialogAboutBinding
import com.flatcode.beautytouch.databinding.DialogAppBinding
import com.flatcode.beautytouch.databinding.DialogCloseappBinding
import com.flatcode.beautytouch.databinding.DialogLogoutBinding
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.navigation.NavigationView
import io.selimdawa.bubblebottom.BubbleBottomNavigation
import io.selimdawa.bubblebottom.Model
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var activity: Activity? = null
    var context: Context = also { activity = it }
    var home = "Home Page"
    var skin_product = "Skin Products"
    var hair_product = "Hair Products"
    var shopping_center = "Shopping Centers"
    var number_product = DATA.EMPTY
    var bottomNavigation: BubbleBottomNavigation? = null
    var publisher: String = DATA.PUBLISHER_NAME
    var aname: String = DATA.APP_NAME

    private val userViewModel: UserViewModel by viewModels()
    private val postViewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.root.updatePadding(top = systemBars.top)
            binding.bottomNavigation.updatePadding(bottom = systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.toolbar.image.setOnClickListener { context.openActivity<ProfileActivity>() }
        binding.toolbar.drawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        MobileAds.initialize(this) { }
        activity!!.interstitialAd()

        binding.myProfile.setOnClickListener {
            context.openActivity<ProfileActivity>()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.favorites.setOnClickListener { context.openActivity<FavoritesActivity>() }
        binding.messenger.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://wa.me/message/E2YOU4NVTIEAD1")
            startActivity(i)
        }
        binding.reward.setOnClickListener { context.openActivity<RewardActivity>() }
        binding.aboutApp.setOnClickListener { showDialogAboutApp() }
        binding.shareApp.setOnClickListener { ShareApp() }
        binding.aboutMy.setOnClickListener { showDialogAboutMy() }
        binding.logout.setOnClickListener { showDialogLogout() }

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

        bottomNavigation.setCount(1, number_product)
        bottomNavigation.setCount(3, number_product)
        bottomNavigation.setCount(4, number_product)
        bottomNavigation.show(2, true)

        bottomNavigation.setOnClickMenuListener { item: Model ->
            when (item.id) {
                1 -> Toast.makeText(applicationContext, skin_product, Toast.LENGTH_SHORT)
                    .show()

                2 -> {
                    Toast.makeText(applicationContext, home, Toast.LENGTH_SHORT).show()
                }

                3 -> Toast.makeText(applicationContext, hair_product, Toast.LENGTH_SHORT)
                    .show()

                4 -> {
                    Toast.makeText(applicationContext, shopping_center, Toast.LENGTH_SHORT)
                        .show()
                    activity!!.interstitialShow(DATA.INTERSTITIAL_HOME)
                }
            }
        }
        bottomNavigation.setOnReselectListener { item: Model ->
            when (item.id) {
                1 -> Toast.makeText(applicationContext, skin_product, Toast.LENGTH_SHORT)
                    .show()

                2 -> Toast.makeText(applicationContext, home, Toast.LENGTH_SHORT).show()
                3 -> Toast.makeText(applicationContext, hair_product, Toast.LENGTH_SHORT)
                    .show()

                4 -> Toast.makeText(applicationContext, shopping_center, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.imageDrawer.setOnClickListener { context.openActivity<ProfileActivity>() }

        observeViewModels()
        postViewModel.loadCategoryCounts(publisher, aname, DATA.SKIN_PRODUCTS, DATA.HAIR_PRODUCTS, DATA.SHOPPING_CENTERS)
        userViewModel.loadUserInfo()
    }

    private fun observeViewModels() {
        val bottomNavigation = this.bottomNavigation
        lifecycleScope.launch {
            postViewModel.skinCount.collect { resource ->
                Timber.d("Skin count collected: $resource")
                if (resource is Resource.Success) {
                    binding.numberProductSkin.text = MessageFormat.format("{0}", resource.data)
                    bottomNavigation?.setCount(1, resource.data.toString())
                }
            }
        }
        lifecycleScope.launch {
            postViewModel.hairCount.collect { resource ->
                Timber.d("Hair count collected: $resource")
                if (resource is Resource.Success) {
                    binding.numberProductHair.text = MessageFormat.format("{0}", resource.data)
                    bottomNavigation?.setCount(3, resource.data.toString())
                }
            }
        }
        lifecycleScope.launch {
            postViewModel.shoppingCount.collect { resource ->
                Timber.d("Shopping count collected: $resource")
                if (resource is Resource.Success) {
                    binding.numberShoppingCenters.text = MessageFormat.format("{0}", resource.data)
                    bottomNavigation?.setCount(4, resource.data.toString())
                }
            }
        }
        lifecycleScope.launch {
            userViewModel.userInfo.collect { resource ->
                Timber.d("User info collected: $resource")
                if (resource is Resource.Success) {
                    val user = resource.data
                    binding.imageDrawer.load(user.imageurl)
                    binding.toolbar.image.load(user.imageurl)
                    binding.name.text = user.username
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val dialogBinding = DialogCloseappBinding.inflate(layoutInflater)
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialogBinding.yes.setOnClickListener { finish() }
            dialogBinding.no.setOnClickListener { dialog.cancel() }
            dialog.show()
            dialog.window!!.attributes = lp
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun ShareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            " Little Beauty: beauty care application, download it now from Google Play " + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
        )
        startActivity(Intent.createChooser(shareIntent, "Choose how to share"))
    }

    private fun showDialogAboutMy() {
        val dialogBinding = DialogAboutBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        lifecycleScope.launch {
            userViewModel.appTools.collect { resource ->
                if (resource is Resource.Success) {
                    val tools = resource.data
                    dialogBinding.image.loadImage(true, tools.imageMe)
                    dialogBinding.text.text = tools.aboutMe
                }
            }
        }
        userViewModel.loadAppTools()

        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showDialogAboutApp() {
        val dialogBinding = DialogAppBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogBinding.linearRate.setOnClickListener { activity!!.rateUs() }
        dialogBinding.facebookDesign.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(openFacebookIntent)
            }

            val openFacebookIntent: Intent
                get() = try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0)
                    Intent(Intent.ACTION_VIEW, Uri.parse(DATA.FB_DESINGER))
                } catch (e: Exception) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(DATA.FB_DESINGER_2))
                }
        })
        dialogBinding.facebookProgrammer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(openFacebookIntent)
            }

            val openFacebookIntent: Intent
                get() = try {
                    getPackageManager().getPackageInfo("com.facebook.katana", 0)
                    Intent(Intent.ACTION_VIEW, Uri.parse(DATA.FB_PROGRAMMER))
                } catch (e: Exception) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(DATA.FB_PROGRAMMER_2))
                }
        })
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showDialogLogout() {
        val dialogBinding = DialogLogoutBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogBinding.yes.setOnClickListener {
            userViewModel.logout()
            context.openActivity<LoginActivity>(clear = true)
            finish()
        }
        dialogBinding.no.setOnClickListener { dialog.cancel() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    companion object {
        var mInterstitialAd: InterstitialAd? = null
    }
}