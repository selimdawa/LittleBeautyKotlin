package com.flatcode.beautytouch.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.databinding.ActivityRegisterBinding
import com.flatcode.beautytouch.ui.main.MainActivity
import com.flatcode.beautytouch.utils.BaseActivity
import com.flatcode.beautytouch.utils.ProgressDialog
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private var binding: ActivityRegisterBinding? = null
    private val context: Context = this@RegisterActivity
    private val viewModel: RegisterViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding!!.forget.setOnClickListener { context.openActivity<ForgetPasswordActivity>() }
        binding!!.login.setOnClickListener {
            context.openActivity<LoginActivity>()
            finish()
        }
        binding!!.go.setOnClickListener { validateData() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerStatus.collect { result ->
                result?.let {
                    dialog!!.dismiss()
                    if (it.isSuccess) {
                        Toast.makeText(context, "Account created...", Toast.LENGTH_SHORT).show()
                        context.openActivity<MainActivity>(clear = true)
                        finish()
                    } else {
                        Toast.makeText(
                            context,
                            "Registration failed: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    viewModel.resetStatus()
                }
            }
        }
    }

    private fun validateData() {
        val name = binding!!.nameEt.text.toString().trim()
        val email = binding!!.emailEt.text.toString().trim()
        val password = binding!!.passwordEt.text.toString().trim()
        val cPassword = binding!!.cPasswordEt.text.toString().trim()

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter your name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(context, "Confirm password...!", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(context, "Password doesn't match...!", Toast.LENGTH_SHORT).show()
        } else {
            dialog!!.setMessage("Creating account...")
            dialog!!.show()
            viewModel.register(name, email, password)
        }
    }
}