package com.flatcode.beautytouchadmin.ui.profile

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.flatcode.beautytouchadmin.utils.BaseActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.canhub.cropper.CropImageContract
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.databinding.ActivityAboutMeBinding
import com.flatcode.beautytouchadmin.ui.other.ToolsViewModel
import com.flatcode.beautytouchadmin.utils.checkStoragePermission
import com.flatcode.beautytouchadmin.utils.cropImageSquareOptions
import com.flatcode.beautytouchadmin.utils.loadImage
import com.flatcode.beautytouchadmin.utils.setMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.flatcode.beautytouchadmin.utils.loadingDialog

@AndroidEntryPoint
class AboutMeActivity : BaseActivity() {

    private var binding: ActivityAboutMeBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: Dialog? = null
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
        cropImage.launch(cropImageSquareOptions())
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            binding!!.image.setImageURI(imageUri)
        } else {
            val error = result.error
            Toast.makeText(this, "Something went wrong! $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutMeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = loadingDialog(context)

        binding!!.toolbar.nameSpace.setText(R.string.about_me)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.go.setOnClickListener { validateData() }
        binding!!.show.setOnClickListener { showDialogAboutMy() }
        binding!!.editImageIcon.setOnClickListener {
            checkStoragePermission(requestPermissionLauncher) { pickImage() }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tools.collect { tools ->
                    tools?.let {
                        binding!!.image.loadImage(true, it.imageMe)
                        binding!!.name.setText(it.aboutMe)
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
            dialog!!.setMessage(getString(R.string.loading))
            dialog!!.show()
            viewModel.updateAboutMe(name, imageUri)
        }
    }

    private fun showDialogAboutMy() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_about)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val image = dialog.findViewById<ImageView>(R.id.image)
        val text = dialog.findViewById<TextView>(R.id.text)

        viewModel.tools.value?.let {
            image.loadImage(true, it.imageMe)
            text.text = it.aboutMe
        }

        dialog.show()
        dialog.window!!.attributes = lp
    }
}
