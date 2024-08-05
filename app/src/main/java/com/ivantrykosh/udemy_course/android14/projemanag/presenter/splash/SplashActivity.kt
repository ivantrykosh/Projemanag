package com.ivantrykosh.udemy_course.android14.projemanag.presenter.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ivantrykosh.udemy_course.android14.projemanag.activities.MainActivity
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivitySplashBinding
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseActivity
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.BaseViewModel
import com.ivantrykosh.udemy_course.android14.projemanag.presenter.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private val baseViewModel: BaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        hideStatusBar()

        val typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface

        Handler(Looper.getMainLooper()).postDelayed({
            baseViewModel.getCurrentUserId()
            baseViewModel.getCurrentUserIdState.observe(this) { state ->
                when {
                    state.loading -> { }
                    state.error.isNotEmpty() -> showErrorSnackBar(state.error)
                    else -> {
                        val userLoggedIn = state.data!!.isNotEmpty()
                        launchActivity(userLoggedIn)
                    }
                }
            }
        }, 2500)
    }

    private fun launchActivity(userLoggedIn: Boolean) {
        if (userLoggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}