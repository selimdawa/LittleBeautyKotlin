package com.flatcode.beautytouchadmin.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var binding: ActivitySplashBinding? = null
    private val viewModel: SplashViewModel by viewModels()
    private val timeFinal = 2000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = ActivitySplashBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            delay(timeFinal)
            checkUser()
        }
    }

    private fun checkUser() {
        if (!viewModel.isUserLoggedIn()) {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
