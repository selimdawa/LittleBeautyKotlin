package com.flatcode.littlebeautyadmin.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlebeautyadmin.R
import com.flatcode.littlebeautyadmin.databinding.ActivityAboutMeBinding
import com.flatcode.littlebeautyadmin.ui.other.ToolsViewModel
import com.flatcode.littlebeautyadmin.utils.BaseActivity
import com.flatcode.littlebeautyadmin.utils.DATA
import com.flatcode.littlebeautyadmin.utils.ProgressDialog
import com.flatcode.littlebeautyadmin.utils.checkStoragePermission
import com.flatcode.littlebeautyadmin.utils.cropImage
import com.flatcode.littlebeautyadmin.utils.loadImage
import com.flatcode.littlebeautyadmin.utils.pickImage
import com.flatcode.littlebeautyadmin.utils.showAboutMeDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AboutMeActivity : BaseActivity() {

    private var binding: ActivityAboutMeBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var isDataLoaded = false
    private var dialog: ProgressDialog? = null
    private val viewModel: ToolsViewModel by viewModels()

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
        pickImage(DATA.MIN_SQUARE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DATA.MIN_SQUARE) {
            if (resultCode == RESULT_OK && data != null) {
                val uri = data.data
                if (uri != null) {
                    imageUri = uri
                    cropImage(
                        uri = uri,
                        aspectRatioX = 1,
                        aspectRatioY = 1,
                        isOval = true,
                        minWidth = DATA.MIN_SQUARE,
                        minHeight = DATA.MIN_SQUARE,
                        requestCode = DATA.MIN_SQUARE
                    )
                } else {
                    val resultUri =
                        IntentCompat.getParcelableExtra(data, "CROP_RESULT_URI", Uri::class.java)
                    if (resultUri != null) {
                        imageUri = resultUri
                        binding!!.image.setImageURI(imageUri)
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                imageUri = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutMeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("IS_DATA_LOADED", false)
            imageUri = BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI", Uri::class.java)
            imageUri?.let { binding!!.image.setImageURI(it) }
        }

        dialog = ProgressDialog(context).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.toolbar.nameSpace.setText(R.string.about_me)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }
        binding!!.show.setOnClickListener { showDialogAboutMy() }
        binding!!.editImageIcon.setOnClickListener {
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_DATA_LOADED", isDataLoaded)
        imageUri?.let { outState.putParcelable("IMAGE_URI", it) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tools.collect { tools ->
                    tools?.let {
                        if (!isDataLoaded) {
                            binding!!.image.loadImage(true, it.imageMe)
                            binding!!.name.setText(it.aboutMe)
                            isDataLoaded = true
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog?.dismiss()
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        imageUri = null
                        isDataLoaded = false
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val name = binding!!.name.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(context, R.string.enter_name, Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage(getString(R.string.loading))
            dialog?.show()
            viewModel.updateAboutMe(name, imageUri)
        }
    }

    private fun showDialogAboutMy() {
        viewModel.tools.value?.let {
            showAboutMeDialog(it.imageMe, it.aboutMe)
        }
    }
}