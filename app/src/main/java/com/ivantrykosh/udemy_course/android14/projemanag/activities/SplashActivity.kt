package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivitySplashBinding
import com.ivantrykosh.udemy_course.android14.projemanag.firebase.Firestore

class SplashActivity : BaseActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

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
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typeface

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserId = Firestore().getCurrentUserId()
            if (currentUserId.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }, 2500)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}