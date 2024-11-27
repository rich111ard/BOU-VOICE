package com.bou.bouvoice.ui.initial

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.MainActivity
import com.bou.bouvoice.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private lateinit var loadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Set up ProgressDialog for loading
        loadingDialog = ProgressDialog(this).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }

        setupEmailAutoCompletion()
        setupPasswordVisibilityToggle()

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.register.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            if (email.isEmpty() || !email.endsWith("@bou.or.ug")) {
                Toast.makeText(this, "Please enter your work email to reset password", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(email)
            }
        }
    }

    private fun setupEmailAutoCompletion() {
        binding.emailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val text = it.toString()
                    if (!text.endsWith("@bou.or.ug") && !text.contains("@")) {
                        binding.emailInput.setText("$text@bou.or.ug")
                        binding.emailInput.setSelection(text.length)
                    }
                }
            }
        })
    }

    private fun setupPasswordVisibilityToggle() {
        binding.passwordEyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible, binding.passwordInput, binding.passwordEyeIcon)
        }
    }

    private fun togglePasswordVisibility(isVisible: Boolean, passwordField: android.widget.EditText, toggleIcon: ImageView) {
        if (isVisible) {
            passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleIcon.setImageResource(android.R.drawable.ic_menu_view)
        } else {
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleIcon.setImageResource(android.R.drawable.ic_secure)
        }
        passwordField.setSelection(passwordField.text.length)
    }

    private fun loginUser() {
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()

        when {
            email.isEmpty() -> {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                binding.emailInput.requestFocus()
                return
            }
            !email.endsWith("@bou.or.ug") -> {
                Toast.makeText(this, "Please use your work email ending with '@bou.or.ug'", Toast.LENGTH_SHORT).show()
                binding.emailInput.requestFocus()
                return
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                binding.passwordInput.requestFocus()
                return
            }
        }

        loadingDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    saveUserCredentials(email)
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset link: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserCredentials(email: String) {
        val sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("loggedInUserEmail", email)
            putBoolean("isLoggedIn", true)
            apply()
        }
    }
}
