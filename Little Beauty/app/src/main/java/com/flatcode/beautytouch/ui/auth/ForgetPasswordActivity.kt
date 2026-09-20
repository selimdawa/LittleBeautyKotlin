package com.flatcode.beautytouch.ui.auth

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.openActivity
import com.flatcode.beautytouch.databinding.ActivityForgetPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ForgetPasswordActivity : AppCompatActivity() {

    private var binding: ActivityForgetPasswordBinding? = null
    private val context: Context = this@ForgetPasswordActivity
    private val viewModel: AuthViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top, bottom = systemBars.bottom)
            insets
        }

        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding!!.noAccount.setOnClickListener {
            context.openActivity<RegisterActivity>()
            finish()
        }
        binding!!.login.setOnClickListener {
            context.openActivity<LoginActivity>()
            finish()
        }
        binding!!.go.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forgetPasswordState.collect { resource ->
                Timber.d("Forget password state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog!!.setMessage("Password recovery is sent...")
                        dialog!!.show()
                    }

                    is Resource.Success -> {
                        dialog!!.dismiss()
                        Toast.makeText(context, resource.data, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Error -> {
                        dialog!!.dismiss()
                        Timber.e("Forget password error: ${resource.message}")
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private var number = "0111111111"
    private var email = ""
    private fun validateDate() {

        //get data
        email = binding!!.emailEt.text.toString().trim { it <= ' ' } + "@flatcodetest.com"
        number = binding!!.emailEt.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            Toast.makeText(context, "Enter the Phone number!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.PHONE.matcher(email).matches()) {
            Toast.makeText(context, "Phone number is incorrect!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(number)) {
            Toast.makeText(context, "Phone number is incorrect!", Toast.LENGTH_SHORT).show()
        } else if (number.length != 10) {
            Toast.makeText(context, "Please enter a 10 digit number!", Toast.LENGTH_SHORT).show()
        } else if (number == "0111111111") {
            Toast.makeText(context, "Please enter a valid number!", Toast.LENGTH_SHORT).show()
        } else {
            val digit = number[0].toString().toInt()
            val digit2 = number[1].toString().toInt()
            val digit3 = number[2].toString().toInt()
            if (digit == 0 && digit2 == 9 && (digit3 == 3 || digit3 == 4 || digit3 == 5 || digit3 == 6 || digit3 == 8 || digit3 == 9)) {
                viewModel.forgetPassword(email)
            } else {
                Toast.makeText(
                    context, "Please enter a valid phone number and password! ", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}