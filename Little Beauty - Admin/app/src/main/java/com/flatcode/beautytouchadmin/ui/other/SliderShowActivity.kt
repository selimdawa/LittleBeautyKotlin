package com.flatcode.beautytouchadmin.ui.other

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.flatcode.beautytouchadmin.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canhub.cropper.CropImageContract
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivitySliderShowBinding
import com.flatcode.beautytouchadmin.utils.checkStoragePermission
import com.flatcode.beautytouchadmin.utils.cropImageSliderOptions
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.setMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.flatcode.beautytouchadmin.utils.loadingDialog

@AndroidEntryPoint
class SliderShowActivity : BaseActivity() {

    private var binding: ActivitySliderShowBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: Dialog? = null
    private var imageNumber = 0
    private val viewModel: SliderViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            pickImage()
        } else {
            Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImage() {
        cropImage.launch(cropImageSliderOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            dialog!!.setMessage("Posting photo...")
            dialog!!.show()
            viewModel.uploadSlider(imageNumber.toString(), imageUri!!)
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.toolbar.nameSpace.setText(R.string.slider_show)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        dialog = loadingDialog(context)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        val buttons = listOf(
            binding!!.addOne,
            binding!!.addTwo,
            binding!!.addThree,
            binding!!.addFour,
            binding!!.addFive,
            binding!!.addSix,
            binding!!.addSeven,
            binding!!.addEight,
            binding!!.addNine,
            binding!!.addTeen,
            binding!!.addEleven,
            binding!!.addTwelfth,
            binding!!.addThirteen,
            binding!!.addFourteenth,
            binding!!.addFifteenth,
            binding!!.addSixteen,
            binding!!.addSeventeen,
            binding!!.addEighteen,
            binding!!.addNineteen,
            binding!!.addTwenty
        )
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                imageNumber = index + 1
                checkStoragePermission(requestPermissionLauncher) { pickImage() }
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sliders.collect { sliders ->
                    updateUI(sliders)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog!!.dismiss()
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateUI(sliders: Map<String, String>) {
        val count = sliders.size
        binding!!.toolbar.nameSpace.text = getString(R.string.slider_show_format, count)

        val images = listOf(
            binding!!.imageOne,
            binding!!.imageTwo,
            binding!!.imageThree,
            binding!!.imageFour,
            binding!!.imageFive,
            binding!!.imageSix,
            binding!!.imageSeven,
            binding!!.imageEight,
            binding!!.imageNine,
            binding!!.imageTeen,
            binding!!.imageEleven,
            binding!!.imageTwelfth,
            binding!!.imageThirteen,
            binding!!.imageFourteenth,
            binding!!.imageFifteenth,
            binding!!.imageSixteen,
            binding!!.imageSeventeen,
            binding!!.imageEighteen,
            binding!!.imageNineteen,
            binding!!.imageTwenty
        )
        val linearLayouts = listOf(
            binding!!.linearOne,
            binding!!.linearTwo,
            binding!!.linearThree,
            binding!!.linearFour,
            binding!!.linearFive,
            binding!!.linearSix,
            binding!!.linearSeven,
            binding!!.linearEight,
            binding!!.linearNine,
            binding!!.linearTeen,
            binding!!.linearEleven,
            binding!!.linearTwelfth,
            binding!!.linearThirteen,
            binding!!.linearFourteenth,
            binding!!.linearFifteenth,
            binding!!.linearSixteen,
            binding!!.linearSeventeen,
            binding!!.linearEighteen,
            binding!!.linearNineteen,
            binding!!.linearTwenty
        )

        for (i in 0 until 20) {
            val key = (i + 1).toString()
            val url = sliders[key] ?: ""
            images[i].loadImage(false, url)
            linearLayouts[i].visibility = if (count >= i) View.VISIBLE else View.GONE
        }
        binding!!.bar.visibility = View.GONE
    }
}
