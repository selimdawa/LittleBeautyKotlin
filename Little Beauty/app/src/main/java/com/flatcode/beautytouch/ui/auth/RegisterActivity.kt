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
import com.flatcode.beautytouch.ui.main.MainActivity
import com.flatcode.beautytouch.utils.Resource
import com.flatcode.beautytouch.utils.Intent1
import com.flatcode.beautytouch.utils.IntentClear
import com.flatcode.beautytouch.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    var context: Context = this@RegisterActivity
    private val viewModel: AuthViewModel by viewModels()
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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

        binding!!.login.setOnClickListener {
            context.Intent1(LoginActivity::class.java)
            finish()
        }
        binding!!.forget.setOnClickListener { context.Intent1(ForgetPasswordActivity::class.java) }
        binding!!.go.setOnClickListener { validateData() }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { resource ->
                Timber.d("Register state collected: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        dialog!!.setMessage("The account is created")
                        dialog!!.show()
                    }

                    is Resource.Success -> {
                        dialog!!.dismiss()
                        Toast.makeText(context, "Account created", Toast.LENGTH_SHORT).show()
                        context.IntentClear(MainActivity::class.java)
                        finish()
                    }

                    is Resource.Error -> {
                        dialog!!.dismiss()
                        Timber.e("Register error: ${resource.message}")
                        Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""
    private var number = "0111111111"
    private fun validateData() {

        //get data
        name = binding!!.nameEt.text.toString().trim { it <= ' ' }
        number = binding!!.phoneEt.text.toString().trim { it <= ' ' }
        email = binding!!.phoneEt.text.toString().trim { it <= ' ' } + "@flatcodetest.com"
        password = binding!!.passwordEt.text.toString().trim { it <= ' ' }
        val cPassword = binding!!.cPasswordEt.text.toString().trim { it <= ' ' }

        //validate data
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(context, "Enter the username!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.PHONE.matcher(number).matches()) {
            Toast.makeText(context, "Enter the Phone number!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter the password!", Toast.LENGTH_SHORT).show()
        } else if (TextUtils.isEmpty(cPassword)) {
            Toast.makeText(context, "Confirm password!", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            Toast.makeText(context, "Password does not match!", Toast.LENGTH_SHORT).show()
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
                viewModel.register(name, email, password, number)
            } else {
                Toast.makeText(context, "Please enter valid data!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}