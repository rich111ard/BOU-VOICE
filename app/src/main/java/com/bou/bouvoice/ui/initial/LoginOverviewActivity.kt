package com.bou.bouvoice.ui.initial

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.databinding.ActivityLoginOverviewBinding

class LoginOverviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOverviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // New User Button - Navigate to Registration Page
        binding.btnNewUser.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Existing User Button - Navigate to Login Page
        binding.btnExistingUser.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
