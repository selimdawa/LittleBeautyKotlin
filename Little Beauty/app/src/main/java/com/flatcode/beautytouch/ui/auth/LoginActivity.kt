package com.flatcode.beautytouch.ui.auth

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.databinding.ActivityLoginBinding
import com.flatcode.beautytouch.ui.main.MainActivity
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    var context: Context = this@LoginActivity
    private val viewModel: AuthViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.forget.setOnClickListener {
            context.openActivity<ForgetPasswordActivity>()
        }
        binding.noAccount.setOnClickListener {
            context.openActivity<RegisterActivity>()
        }
        binding.loginBtn.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { resource ->
                Timber.d("Login state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog!!.setMessage("Signed in...")
                        dialog!!.show()
                    }

                    is Resource.Success -> {
                        dialog!!.dismiss()
                        context.openActivity<MainActivity>(clear = true)
                    }

                    is Resource.Error -> {
                        dialog!!.dismiss()
                        Timber.e("Login error: ${resource.message}")
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private var number = "0111111111"
    private var email = ""
    private var password = ""
    private fun validateDate() {

        //get data
        email = binding.emailEt.text.toString().trim { it <= ' ' } + "@flatcodetest.com"
        number = binding.emailEt.text.toString().trim { it <= ' ' }
        password = binding.passwordEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Password entry error!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(context, "Error entering the number!", Toast.LENGTH_SHORT).show()
        } else if (number.length != 10) {
            Toast.makeText(context, "Please enter a 10 digit number!", Toast.LENGTH_SHORT).show()
        } else if (number == "0111111111") {
            Toast.makeText(context, "Please enter a valid number!", Toast.LENGTH_SHORT).show()
        } else {
            val digit = number[0].toString().toInt()
            val digit2 = number[1].toString().toInt()
            val digit3 = number[2].toString().toInt()
            if (digit == 0 && digit2 == 9 && (digit3 == 3 || digit3 == 4 || digit3 == 5 || digit3 == 6 || digit3 == 8 || digit3 == 9)) {
                viewModel.login(email, password)
            } else {
                Toast.makeText(
                    context, "Please enter a valid phone number and password! ", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}