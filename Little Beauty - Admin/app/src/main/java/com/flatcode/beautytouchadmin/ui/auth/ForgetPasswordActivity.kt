package com.flatcode.beautytouchadmin.ui.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.databinding.ActivityForgetPasswordBinding
import com.flatcode.beautytouchadmin.utils.BaseActivity
import com.flatcode.beautytouchadmin.utils.ProgressDialog
import com.flatcode.beautytouchadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordActivity : BaseActivity() {

    private var binding: ActivityForgetPasswordBinding? = null
    private val context: Context = this@ForgetPasswordActivity
    private var dialog: ProgressDialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.login.setOnClickListener {
            openActivity<LoginActivity>()
            finish()
        }
        binding!!.go.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog?.dismiss()
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Failed: " + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private var email = ""
    private fun validateDate() {
        email = binding!!.emailEt.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(context, "Enter email...!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email format...!", Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage("Sending password recovery instructions to $email")
            dialog?.show()
            viewModel.forgetPassword(email)
        }
    }
}