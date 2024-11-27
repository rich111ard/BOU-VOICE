package com.bou.bouvoice.ui.menuItem

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.bou.bouvoice.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeFullNameDialog(context: Context) : Dialog(context) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var prefixSpinner: Spinner
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var loadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_full_name)

        // Set the dialog to use a larger portion of the screen
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Initialize Firebase and UI components
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        prefixSpinner = findViewById(R.id.prefix_spinner)!!
        firstNameInput = findViewById(R.id.first_name)!!
        lastNameInput = findViewById(R.id.last_name)!!

        // Initialize the loading dialog
        loadingDialog = ProgressDialog(context).apply {
            setMessage("Saving changes...")
            setCancelable(false)
        }

        setupPrefixSpinner()
        loadUserDetails()

        // Set up save and cancel button actions
        findViewById<Button>(R.id.save_full_name_button)?.setOnClickListener { saveChanges() }
        findViewById<Button>(R.id.cancel_button)?.setOnClickListener { dismiss() }
    }

    // Function to set up the prefix spinner with "Choose Prefix" as the default and no saved prefix
    private fun setupPrefixSpinner() {
        val prefixes = arrayOf("Choose Prefix", "Dr", "Mr", "Ms", "Eng", "Prof", "Hon", "Rev", "Sir", "Madam", "Lord")
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, prefixes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prefixSpinner.adapter = adapter
        prefixSpinner.setSelection(0) // Ensure "Choose Prefix" is selected initially
    }

    // Function to load only the user's first and last names without any prefix
    private fun loadUserDetails() {
        val userEmail = auth.currentUser?.email ?: return
        firestore.collection("users").document(userEmail).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""

                    // Set values in fields; leave prefixSpinner as "Choose Prefix"
                    firstNameInput.setText(firstName)
                    lastNameInput.setText(lastName)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to save changes to Firebase, enforcing prefix selection
    private fun saveChanges() {
        val selectedPrefix = prefixSpinner.selectedItem.toString()
        val firstName = firstNameInput.text.toString().trim()
        val lastName = lastNameInput.text.toString().trim()

        // Validation checks
        if (selectedPrefix == "Choose Prefix") {
            Toast.makeText(context, "Please select a prefix", Toast.LENGTH_SHORT).show()
            return
        }
        if (firstName.isEmpty()) {
            Toast.makeText(context, "First name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        if (lastName.isEmpty()) {
            Toast.makeText(context, "Last name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading dialog
        loadingDialog.show()

        val userEmail = auth.currentUser?.email ?: return

        // Prepare data for update
        val fullName = "$selectedPrefix $firstName $lastName"
        val updates = mapOf(
            "prefix" to selectedPrefix,
            "firstName" to firstName,
            "lastName" to lastName,
            "fullName" to fullName
        )

        // Update user information in Firestore
        firestore.collection("users").document(userEmail).update(updates)
            .addOnSuccessListener {
                loadingDialog.dismiss()
                Toast.makeText(context, "Details updated successfully", Toast.LENGTH_SHORT).show()
                dismiss() // Close dialog after successful update
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()
                Toast.makeText(context, "Failed to update details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
