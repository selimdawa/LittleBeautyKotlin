package com.flatcode.beautytouchadmin.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.databinding.ActivityLoginBinding
import com.flatcode.beautytouchadmin.ui.main.MainActivity
import com.flatcode.beautytouchadmin.utils.BaseActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.ProgressDialog
import com.flatcode.beautytouchadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val context: Context = this@LoginActivity
    private var dialog: ProgressDialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = ProgressDialog(this).apply {
            setTitle("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        binding.forget.setOnClickListener { openActivity<ForgetPasswordActivity>() }
        binding.loginBtn.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog?.dismiss()
                    result.onSuccess {
                        openActivity<MainActivity>(clear = true)
                    }.onFailure {
                        Toast.makeText(context, DATA.EMPTY + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateDate() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "Invalid email pattern...!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password...!", Toast.LENGTH_SHORT).show()
        } else {
            dialog?.setMessage("Logging In...")
            dialog?.show()
            viewModel.login(email, password)
        }
    }
}