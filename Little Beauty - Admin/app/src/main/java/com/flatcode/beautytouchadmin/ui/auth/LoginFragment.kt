package com.flatcode.beautytouchadmin.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.flatcode.beautytouchadmin.R
import com.flatcode.beautytouchadmin.utils.DATA
import com.flatcode.beautytouchadmin.utils.Dialog as MyDialog
import com.flatcode.beautytouchadmin.utils.setMessage
import com.flatcode.beautytouchadmin.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding: ActivityLoginBinding? = null
    private var dialog: Dialog? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = ActivityLoginBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = MyDialog.loadingDialog(requireContext())

        binding!!.forget.setOnClickListener {
            // TODO: Navigate to Forget Password Fragment when created
        }
        binding!!.loginBtn.setOnClickListener { validateDate() }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.actionStatus.collect { result ->
                    dialog!!.dismiss()
                    result.onSuccess {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }.onFailure {
                        Toast.makeText(requireContext(), DATA.EMPTY + it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun validateDate() {
        val number = binding!!.phoneEt.text.toString().trim()
        val password = binding!!.passwordEt.text.toString().trim()

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Password entry error!", Toast.LENGTH_SHORT).show()
        } else if (number.isEmpty()) {
            Toast.makeText(requireContext(), "Error entering the phone number!", Toast.LENGTH_SHORT).show()
        } else if (number.length != 10) {
            Toast.makeText(requireContext(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show()
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
                    requireContext(), "Please enter a valid phone number and password!", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
