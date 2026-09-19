package com.flatcode.beautytouchadmin.ui.auth

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.beautytouchadmin.utils.openActivity
import com.flatcode.beautytouchadmin.databinding.ActivityForgetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {

    private var binding: ActivityForgetPasswordBinding? = null
    private val context: Context = this@ForgetPasswordActivity
    private var dialog: ProgressDialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

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
                    dialog!!.dismiss()
                    result.onSuccess {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }.onFailure {
                        Toast.makeText(context, "Something went wrong!" + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateDate() {
        val number = binding!!.phoneEt.text.toString().trim()
        val email = "$number@flatcodetest.com"

        if (number.isEmpty()) {
            Toast.makeText(context, "Enter the number!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.PHONE.matcher(number).matches()) {
            Toast.makeText(context, "Phone number is incorrect!", Toast.LENGTH_SHORT).show()
        } else if (number.length != 10) {
            Toast.makeText(context, "Please enter a valid phone number!", Toast.LENGTH_SHORT).show()
        } else {
            val digit = number[0].toString().toInt()
            val digit2 = number[1].toString().toInt()
            val digit3 = number[2].toString().toInt()
            if (digit == 0 && digit2 == 9 && (digit3 == 3 || digit3 == 4 || digit3 == 5 || digit3 == 6 || digit3 == 8 || digit3 == 9)) {
                dialog!!.setMessage("Password recovery is sent to $email")
                dialog!!.show()
                viewModel.forgetPassword(number)
            } else {
                Toast.makeText(
                    context, "Please enter a valid phone number and password!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
