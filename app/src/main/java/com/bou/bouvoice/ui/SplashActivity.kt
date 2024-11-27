package com.bou.bouvoice

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.ui.initial.LoginOverviewActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser != null) {
                // User is logged in, navigate to MainActivity
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            } else {
                // User is not logged in, navigate to LoginOverviewActivity
                val loginIntent = Intent(this, LoginOverviewActivity::class.java)
                startActivity(loginIntent)
            }
            finish() // Close the SplashActivity
        }, 2000) // Delay for 2 seconds (optional)
    }
}
