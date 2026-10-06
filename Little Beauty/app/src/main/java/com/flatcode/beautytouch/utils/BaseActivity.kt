package com.flatcode.beautytouch.utils

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.flatcode.beautytouch.R

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        view?.let { root ->
            ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
                val systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                val toolbar = v.findViewById<View>(R.id.toolbar)
                val bottomNav = v.findViewById<View>(R.id.bottomNavigation)
                val navView = v.findViewById<View>(R.id.nav_view)

                if (toolbar != null || bottomNav != null || navView != null) {
                    toolbar?.setPadding(
                        toolbar.paddingLeft,
                        systemBars.top,
                        toolbar.paddingRight,
                        toolbar.paddingBottom
                    )
                    bottomNav?.setPadding(
                        bottomNav.paddingLeft,
                        bottomNav.paddingTop,
                        bottomNav.paddingRight,
                        systemBars.bottom
                    )
                    navView?.setPadding(
                        navView.paddingLeft,
                        systemBars.top,
                        navView.paddingRight,
                        systemBars.bottom
                    )
                } else {
                    v.setPadding(
                        systemBars.left,
                        systemBars.top,
                        systemBars.right,
                        systemBars.bottom
                    )
                }
                insets
            }
        }
    }
}