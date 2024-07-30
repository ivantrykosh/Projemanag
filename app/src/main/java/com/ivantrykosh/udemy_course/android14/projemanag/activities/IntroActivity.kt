package com.ivantrykosh.udemy_course.android14.projemanag.activities

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ivantrykosh.udemy_course.android14.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private var _binding: ActivityIntroBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityIntroBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding.btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.btnSignInIntro.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}