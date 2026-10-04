package com.flatcode.beautytouch.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.beautytouch.databinding.ActivityProfileBinding
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.ProgressDialog
import com.flatcode.beautytouch.utils.isNetworkAvailable
import com.flatcode.beautytouch.utils.startCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    private val viewModel: UserViewModel by viewModels()

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.let { intent ->
                    IntentCompat.getParcelableExtra(intent, "CROP_RESULT_URI", Uri::class.java)
                }
                binding.image.setImageURI(null)
                binding.image.setImageURI(imageUri)
                binding.imageTrue.visibility = View.VISIBLE
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                cropImageLauncher.launch(startCropActivity(it, 1, 1, true))
            }
        }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    this, "Permission Denied! Cannot access gallery.", Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.editImageIcon.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        binding.imageEdit.setOnClickListener {
            binding.imageEdit.visibility = View.GONE
            binding.imageClose.visibility = View.VISIBLE
            binding.imageTrue.visibility = View.VISIBLE
            binding.name.visibility = View.GONE
            binding.nameEdit.visibility = View.VISIBLE
        }
        binding.imageClose.setOnClickListener {
            binding.imageEdit.visibility = View.VISIBLE
            binding.imageClose.visibility = View.GONE
            if (imageUri != null) {
                binding.imageTrue.visibility = View.VISIBLE
            } else {
                binding.imageTrue.visibility = View.GONE
            }
            binding.name.visibility = View.VISIBLE
            binding.nameEdit.visibility = View.GONE
        }
        binding.imageTrue.setOnClickListener {
            binding.imageClose.visibility = View.GONE
            binding.imageEdit.visibility = View.VISIBLE
            binding.imageTrue.visibility = View.GONE
            binding.name.visibility = View.VISIBLE
            binding.name.text = binding.nameEdit.text.toString()
            binding.nameEdit.visibility = View.GONE
            validateData()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userInfo.collect { user ->
                user?.let {
                    binding.image.load(it.imageurl)
                    binding.name.text = it.username
                    binding.nameEdit.setText(it.username)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateProfileState.collect { result ->
                result?.let {
                    dialog?.dismiss()
                    if (it.isSuccess) {
                        Toast.makeText(this@ProfileActivity, "Profile Updated", Toast.LENGTH_SHORT)
                            .show()
                        imageUri = null
                    } else {
                        Toast.makeText(
                            this@ProfileActivity, "Error updating profile", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val username = binding.nameEdit.text.toString().trim()
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show()
        } else if (!isNetworkAvailable()) {
            Toast.makeText(
                this,
                getString(com.flatcode.beautytouch.R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val uri = imageUri
            if (uri == null) {
                dialog?.setMessage("Saving changes...")
                dialog?.show()
                viewModel.updateProfile(username)
            } else {
                dialog?.setMessage("Uploading image...")
                dialog?.show()
                viewModel.uploadProfileImageCloudinary(uri) { uploadedUrl ->
                    if (uploadedUrl != null) {
                        viewModel.updateProfile(username, uploadedUrl)
                    } else {
                        dialog?.dismiss()
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo()
    }
}