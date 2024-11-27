package com.bou.bouvoice.ui.initial

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var loadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Set up ProgressDialog
        loadingDialog = ProgressDialog(this).apply {
            setMessage("Please wait...")
            setCancelable(false)
        }

        val prefixes = arrayOf("Dr", "Mr", "Ms", "Eng", "Prof", "Hon", "Rev", "Sir", "Madam", "Lord")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, prefixes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.prefixSpinner.adapter = adapter

        setupEmailAutoCompletion()
        setupPasswordVisibilityToggle()

        binding.btnRegister.setOnClickListener {
            registerUser()
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

        binding.confirmPasswordEyeIcon.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(isConfirmPasswordVisible, binding.confirmPasswordInput, binding.confirmPasswordEyeIcon)
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

    private fun registerUser() {
        val firstName = binding.firstNameInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val prefix = binding.prefixSpinner.selectedItem.toString()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString().trim()
        val confirmPassword = binding.confirmPasswordInput.text.toString().trim()

        // Validate fields with specific messages
        when {
            firstName.isEmpty() -> {
                binding.firstNameInput.error = "First name is required"
                binding.firstNameInput.requestFocus()
                return
            }
            lastName.isEmpty() -> {
                binding.lastNameInput.error = "Last name is required"
                binding.lastNameInput.requestFocus()
                return
            }
            email.isEmpty() -> {
                binding.emailInput.error = "Email is required"
                binding.emailInput.requestFocus()
                return
            }
            !email.endsWith("@bou.or.ug") -> {
                binding.emailInput.error = "Email must end with '@bou.or.ug'"
                binding.emailInput.requestFocus()
                return
            }
            password.isEmpty() -> {
                binding.passwordInput.error = "Password is required"
                binding.passwordInput.requestFocus()
                return
            }
            confirmPassword.isEmpty() -> {
                binding.confirmPasswordInput.error = "Please confirm your password"
                binding.confirmPasswordInput.requestFocus()
                return
            }
            password != confirmPassword -> {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                binding.confirmPasswordInput.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.passwordInput.error = "Password must be at least 6 characters"
                binding.passwordInput.requestFocus()
                return
            }
        }

        loadingDialog.show()

        // Check if the user exists in Firestore
        firestore.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getString("firstName")?.isNotBlank() == true) {
                    loadingDialog.dismiss()
                    Toast.makeText(this, "User already exists with the provided information", Toast.LENGTH_SHORT).show()
                } else {
                    // User does not have full details, proceed to update them
                    val fullName = "$prefix $firstName $lastName"
                    val userUpdates = mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "prefix" to prefix,
                        "fullName" to fullName
                    )

                    firestore.collection("users").document(email).update(userUpdates)
                        .addOnSuccessListener {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    loadingDialog.dismiss()
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "Registration completed successfully", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            loadingDialog.dismiss()
                            Toast.makeText(this, "Failed to update user information: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()
                Toast.makeText(this, "Error fetching user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
