package com.flatcode.beautytouch.ui.profile

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.flatcode.beautytouch.databinding.ActivityProfileBinding
import com.flatcode.beautytouch.utils.LoadingDialog
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.startCropActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private var imageUri: Uri? = null
    private val dialog by lazy { LoadingDialog(this) }

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
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
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
            viewModel.userInfo.collect { resource ->
                Timber.d("User info collected: $resource")
                if (resource is Resource.Success) {
                    val user = resource.data
                    binding.image.load(user.imageurl)
                    binding.name.text = user.username
                    binding.nameEdit.setText(user.username)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uploadImageState.collect { resource ->
                Timber.d("Upload image state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog.show("The image is uploading...")
                    }

                    is Resource.Success -> {
                        dialog.dismiss()
                    }

                    is Resource.Error -> {
                        dialog.dismiss()
                        Timber.e("Upload image error: ${resource.message}")
                        Toast.makeText(this@ProfileActivity, resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            viewModel.updateProfileState.collect { resource ->
                Timber.d("Update profile state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog.show("Saving changes...")
                    }

                    is Resource.Success -> {
                        dialog.dismiss()
                        Toast.makeText(this@ProfileActivity, "Profile Updated", Toast.LENGTH_SHORT)
                            .show()
                        imageUri = null
                    }

                    is Resource.Error -> {
                        dialog.dismiss()
                        Timber.e("Update profile error: ${resource.message}")
                        Toast.makeText(this@ProfileActivity, resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun validateData() {
        val username = binding.nameEdit.text.toString().trim()
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show()
        } else {
            val uri = imageUri
            if (uri == null) {
                viewModel.updateProfile(username)
            } else {
                dialog.show("Uploading image...")
                viewModel.uploadProfileImageCloudinary(uri) { uploadedUrl ->
                    if (uploadedUrl != null) {
                        viewModel.updateProfile(username, uploadedUrl)
                    } else {
                        dialog.dismiss()
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

    override fun onDestroy() {
        super.onDestroy()
        dialog.dismiss()
    }
}