package com.flatcode.beautytouchadmin.ui.auth

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.ui.main.MainActivity
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.Dialog as MyDialog
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.utils.setMessage
import com.flatcode.beautytouchadmin.utils.viewBinding
import com.flatcode.beautytouchadmin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityLoginBinding::inflate)
    private val context: Context = this@LoginActivity
    private var dialog: Dialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        dialog = MyDialog.loadingDialog(context)

        binding.forget.setOnClickListener { openActivity<ForgetPasswordActivity>() }
        binding.loginBtn.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog!!.dismiss()
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
        val number = binding.phoneEt.text.toString().trim()
        val password = binding.passwordEt.text.toString().trim()

        if (password.isEmpty()) {
            Toast.makeText(context, "Password entry error!", Toast.LENGTH_SHORT).show()
        } else if (number.isEmpty()) {
            Toast.makeText(context, "Error entering the phone number!", Toast.LENGTH_SHORT).show()
        } else if (number.length != 10) {
            Toast.makeText(context, "Please enter a valid phone number!", Toast.LENGTH_SHORT).show()
        } else {
            val digit = number[0].toString().toInt()
            val digit2 = number[1].toString().toInt()
            val digit3 = number[2].toString().toInt()
            if (digit == 0 && digit2 == 9 && (digit3 == 3 || digit3 == 4 || digit3 == 5 || digit3 == 6 || digit3 == 8 || digit3 == 9)) {
                dialog!!.setMessage("Signed in...")
                dialog!!.show()
                viewModel.login(number, password)
            } else {
                Toast.makeText(
                    context, "Please enter a valid phone number and password!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
