package com.flatcode.beautytouchadmin.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.core.os.BundleCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityProfileBinding
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
class ProfileActivity : BaseActivity() {

    private var binding: ActivityProfileBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var isDataLoaded = false
    private var dialog: ProgressDialog? = null
    private val viewModel: ProfileViewModel by viewModels()

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
                        binding!!.imageTrue.visibility = View.VISIBLE
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                imageUri = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        if (savedInstanceState != null) {
            isDataLoaded = savedInstanceState.getBoolean("IS_DATA_LOADED", false)
            imageUri = BundleCompat.getParcelable(savedInstanceState, "IMAGE_URI", Uri::class.java)
            imageUri?.let {
                binding!!.image.setImageURI(it)
                binding!!.imageTrue.visibility = View.VISIBLE
            }
        }

        dialog = ProgressDialog(context).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.editImageIcon.setOnClickListener {
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        binding!!.imageEdit.setOnClickListener {
            binding!!.imageEdit.visibility = View.GONE
            binding!!.imageClose.visibility = View.VISIBLE
            binding!!.imageTrue.visibility = View.VISIBLE
            binding!!.name.visibility = View.GONE
            binding!!.nameEdit.visibility = View.VISIBLE
        }
        binding!!.imageClose.setOnClickListener {
            binding!!.imageEdit.visibility = View.VISIBLE
            binding!!.imageClose.visibility = View.GONE
            binding!!.imageTrue.visibility = if (imageUri != null) View.VISIBLE else View.GONE
            binding!!.name.visibility = View.VISIBLE
            binding!!.nameEdit.visibility = View.GONE
        }
        binding!!.imageTrue.setOnClickListener {
            binding!!.imageClose.visibility = View.GONE
            binding!!.imageEdit.visibility = View.VISIBLE
            binding!!.imageTrue.visibility = View.GONE
            binding!!.name.visibility = View.VISIBLE
            binding!!.name.text = binding!!.nameEdit.text.toString()
            binding!!.nameEdit.visibility = View.GONE
            validateData()
        }

        viewModel.loadUserInfo(DATA.FirebaseUserUid)
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
                viewModel.user.collect { user ->
                    user?.let {
                        if (!isDataLoaded) {
                            binding!!.image.loadImage(true, it.imageurl)
                            binding!!.name.text = it.username
                            binding!!.nameEdit.setText(it.username)
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
                        Toast.makeText(
                            context, "Something went wrong! " + it.message, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateData() {
        val username = binding!!.nameEdit.text.toString().trim()
        if (username.isEmpty()) {
            Toast.makeText(context, R.string.enter_name, Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage(getString(R.string.loading))
            dialog?.show()
            viewModel.updateProfile(DATA.FirebaseUserUid, username, imageUri)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isDataLoaded) {
            viewModel.loadUserInfo(DATA.FirebaseUserUid)
        }
    }
}