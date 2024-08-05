package com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.sign_in

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ivantrykosh.udemy_course.android14.projemanag.R
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.FragmentSignInBinding
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    private val signInViewModel: SignInViewModel by viewModels()

    private lateinit var authActivity: AuthActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authActivity = requireActivity() as AuthActivity
        binding.toolbarSignIn.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        authActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
        binding.btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun signInUser() {
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            authActivity.showProgressDialog()
            signInViewModel.signIn(email, password)
            signInViewModel.signInState.observe(viewLifecycleOwner) { signIn ->
                when {
                    signIn.loading -> { }
                    signIn.error.isNotEmpty() -> {
                        authActivity.hideProgressDialog()
                        authActivity.showErrorSnackBar(signIn.error)
                    }
                    else -> {
                        authActivity.loginSuccess()
                    }
                }
            }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
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