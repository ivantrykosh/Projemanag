package com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.sign_up

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentSignUpBinding
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val signUpViewModel: SignUpViewModel by viewModels()

    private lateinit var authActivity: AuthActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authActivity = requireActivity() as AuthActivity
        binding.toolbarSignUp.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        authActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
        binding.btnSignUp.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            authActivity.showProgressDialog()
            signUpViewModel.signUp(email, password, name)
            signUpViewModel.signUpState.observe(viewLifecycleOwner) { signUp ->
                when {
                    signUp.loading -> { }
                    signUp.error.isNotEmpty() -> {
                        authActivity.hideProgressDialog()
                        authActivity.showErrorSnackBar(signUp.error)
                    }
                    else -> {
                        authActivity.loginSuccess()
                    }
                }
            }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() -> {
                authActivity.showErrorSnackBar(getString(R.string.please_enter_name))
                false
            }
            email.isEmpty() -> {
                authActivity.showErrorSnackBar(getString(R.string.please_enter_email))
                false
            }
            password.isEmpty() -> {
                authActivity.showErrorSnackBar(getString(R.string.please_enter_password))
                false
            }
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}