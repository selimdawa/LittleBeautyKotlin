package com.flatcode.beautytouchadmin.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canhub.cropper.CropImageContract
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityProfileBinding
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.checkStoragePermission
import com.flatcode.beautytouchadmin.utils.cropImageSquareOptions
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.setMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.flatcode.beautytouchadmin.utils.Dialog as MyDialog

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null
    private var activity: Activity? = null
    private var context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: Dialog? = null
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
        cropImage.launch(cropImageSquareOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding!!.image.setImageURI(imageUri)
            binding!!.imageTrue.visibility = View.VISIBLE
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
            binding!!.imageTrue.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = MyDialog.loadingDialog(context)

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

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { user ->
                    user?.let {
                        binding!!.image.loadImage(true, it.imageurl)
                        binding!!.name.text = it.username
                        binding!!.nameEdit.setText(it.username)
                    }
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
            dialog!!.setMessage(getString(R.string.loading))
            dialog!!.show()
            viewModel.updateProfile(DATA.FirebaseUserUid, username, imageUri)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserInfo(DATA.FirebaseUserUid)
    }
}
