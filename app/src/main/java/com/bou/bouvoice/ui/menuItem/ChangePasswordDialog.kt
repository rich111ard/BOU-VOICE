package com.bou.bouvoice.ui.menuItem

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bou.bouvoice.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordDialog(context: Context) : Dialog(context) {

    private lateinit var currentPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmNewPassword: EditText
    private lateinit var savePasswordButton: Button
    private lateinit var cancelButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_password)

        // Set the dialog to use a larger portion of the screen
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Initialize Firebase instance
        auth = FirebaseAuth.getInstance()

        // Initialize views
        currentPassword = findViewById(R.id.current_password)!!
        newPassword = findViewById(R.id.new_password)!!
        confirmNewPassword = findViewById(R.id.confirm_new_password)!!
        savePasswordButton = findViewById(R.id.save_password_button)!!
        cancelButton = findViewById(R.id.cancel_button)!!

        // Initialize password toggle icons
        val currentPasswordIcon = findViewById<ImageView>(R.id.current_password_eye_icon)!!
        val newPasswordIcon = findViewById<ImageView>(R.id.new_password_eye_icon)!!
        val confirmPasswordIcon = findViewById<ImageView>(R.id.confirm_new_password_eye_icon)!!

        // Set up toggle listeners for each password field
        currentPasswordIcon.setOnClickListener { togglePasswordVisibility(currentPassword, currentPasswordIcon) }
        newPasswordIcon.setOnClickListener { togglePasswordVisibility(newPassword, newPasswordIcon) }
        confirmPasswordIcon.setOnClickListener { togglePasswordVisibility(confirmNewPassword, confirmPasswordIcon) }

        // Set click listeners
        cancelButton.setOnClickListener { dismiss() }
        savePasswordButton.setOnClickListener { validateAndChangePassword() }
    }

    private fun togglePasswordVisibility(passwordField: EditText, toggleIcon: ImageView) {
        if (passwordField.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            passwordField.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            toggleIcon.setImageResource(android.R.drawable.ic_secure) // Closed-eye icon
        } else {
            passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            toggleIcon.setImageResource(android.R.drawable.ic_menu_view) // Open-eye icon
        }
        passwordField.setSelection(passwordField.text.length) // Keep cursor at the end
    }

    private fun validateAndChangePassword() {
        val currentPass = currentPassword.text.toString().trim()
        val newPass = newPassword.text.toString().trim()
        val confirmPass = confirmNewPassword.text.toString().trim()

        // Validation checks
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPass != confirmPass) {
            Toast.makeText(context, "New password and confirm password do not match", Toast.LENGTH_SHORT).show()
            return
        }
        if (newPass.length < 6) {
            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading dialog
        val progressDialog = AlertDialog.Builder(context)
            .setMessage("Updating password...")
            .setCancelable(false)
            .create()
        progressDialog.show()

        // Re-authenticate the user with the current password
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email!!, currentPass)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                // If re-authentication is successful, update the password
                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed to update password: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(context, "Current password is incorrect", Toast.LENGTH_SHORT).show()
            }
    }
}
