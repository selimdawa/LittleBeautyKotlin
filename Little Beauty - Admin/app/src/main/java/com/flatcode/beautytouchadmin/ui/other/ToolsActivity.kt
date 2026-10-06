package com.flatcode.beautytouchadmin.ui.other

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
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityToolsBinding
import com.flatcode.beautytouchadmin.utils.BaseActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.ProgressDialog
import com.flatcode.beautytouchadmin.utils.checkStoragePermission
import com.flatcode.beautytouchadmin.utils.cropImage
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.pickImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToolsActivity : BaseActivity() {

    private var binding: ActivityToolsBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var imageUri2: Uri? = null
    private var imageUri3: Uri? = null
    private var imageUri4: Uri? = null
    private var isDataLoaded = false
    private var dialog: ProgressDialog? = null
    private var imageNumber = 0
    private val viewModel: ToolsViewModel by viewModels()

    companion object {
        private const val IMAGE_NOW = 1
        private const val IMAGE_OLD = 2
        private const val LOGO_NOW = 3
        private const val LOGO_OLD = 4
    }

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
                        when (imageNumber) {
                            IMAGE_NOW -> {
                                imageUri = resultUri
                                binding!!.imageSessionNow.setImageURI(imageUri)
                            }

                            IMAGE_OLD -> {
                                imageUri2 = resultUri
                                binding!!.imageSessionOld.setImageURI(imageUri2)
                            }

                            LOGO_NOW -> {
                                imageUri3 = resultUri
                                binding!!.logoSessionNow.setImageURI(imageUri3)
                            }

                            LOGO_OLD -> {
                                imageUri4 = resultUri
                                binding!!.logoSessionOld.setImageURI(imageUri4)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("IS_DATA_LOADED", false)
            imageNumber = savedInstanceState.getInt("IMAGE_NUMBER", 0)
            imageUri = BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI", Uri::class.java)
            imageUri2 =
                BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI_2", Uri::class.java)
            imageUri3 =
                BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI_3", Uri::class.java)
            imageUri4 =
                BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI_4", Uri::class.java)
            imageUri?.let { binding!!.imageSessionNow.setImageURI(it) }
            imageUri2?.let { binding!!.imageSessionOld.setImageURI(it) }
            imageUri3?.let { binding!!.logoSessionNow.setImageURI(it) }
            imageUri4?.let { binding!!.logoSessionOld.setImageURI(it) }
        }

        dialog = ProgressDialog(context).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.editImageSessionNow.setOnClickListener {
            imageNumber = IMAGE_NOW
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editImageSessionOld.setOnClickListener {
            imageNumber = IMAGE_OLD
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editLogoSessionNow.setOnClickListener {
            imageNumber = LOGO_NOW
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.editLogoSessionOld.setOnClickListener {
            imageNumber = LOGO_OLD
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }
        binding!!.toolbar.nameSpace.setText(R.string.tools)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }

        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("IS_DATA_LOADED", isDataLoaded)
        outState.putInt("IMAGE_NUMBER", imageNumber)
        imageUri?.let { outState.putParcelable("IMAGE_URI", it) }
        imageUri2?.let { outState.putParcelable("IMAGE_URI_2", it) }
        imageUri3?.let { outState.putParcelable("IMAGE_URI_3", it) }
        imageUri4?.let { outState.putParcelable("IMAGE_URI_4", it) }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tools.collect { tools ->
                    tools?.let {
                        if (!isDataLoaded) {
                            binding!!.imageSessionNow.loadImage(false, it.imageSession)
                            binding!!.imageSessionOld.loadImage(false, it.oldImageSession)
                            binding!!.logoSessionNow.loadImage(false, it.imageLogo)
                            binding!!.logoSessionOld.loadImage(false, it.oldImageLogo)
                            binding!!.sessionNow.setText(it.session)
                            binding!!.sessionOld.setText(it.oldSession)
                            binding!!.sessionNumberNow.setText(it.sessionNumber)
                            binding!!.sessionNumberOld.setText(it.oldSessionNumber)
                            binding!!.yearNow.setText(it.year)
                            binding!!.yearOld.setText(it.oldYear)
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
                        imageUri2 = null
                        imageUri3 = null
                        imageUri4 = null
                        isDataLoaded = false
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val sessionNow = binding!!.sessionNow.text.toString().trim()
        val sessionOld = binding!!.sessionOld.text.toString().trim()
        val sessionNumberNow = binding!!.sessionNumberNow.text.toString().trim()
        val sessionNumberOld = binding!!.sessionNumberOld.text.toString().trim()
        val yearNow = binding!!.yearNow.text.toString().trim()
        val yearOld = binding!!.yearOld.text.toString().trim()

        if (sessionNow.isEmpty()) {
            Toast.makeText(context, "Enter the Session number", Toast.LENGTH_SHORT).show()
        } else if (sessionNumberNow.isEmpty()) {
            Toast.makeText(context, "Enter the Session name", Toast.LENGTH_SHORT).show()
        } else if (yearNow.isEmpty()) {
            Toast.makeText(context, "Enter the year", Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage("Editing....")
            dialog?.show()
            viewModel.updateTools(
                sessionNow,
                sessionOld,
                sessionNumberNow,
                sessionNumberOld,
                yearNow,
                yearOld,
                imageUri,
                imageUri2,
                imageUri3,
                imageUri4
            )
        }
    }
}