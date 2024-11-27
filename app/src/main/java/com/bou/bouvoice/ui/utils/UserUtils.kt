package com.bou.bouvoice.ui.utils // Adjust this line to match your package structure

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UserUtils(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Adds a new user if their email does not already exist in the Firestore 'users' collection.
     * @param email The email of the user to be added.
     * @param userData A map containing the user details (e.g., name, department, role).
     */
    fun addUserIfNotExists(email: String, userData: Map<String, Any>) {
        // Query the 'users' collection for an existing document with the specified email
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documents: QuerySnapshot? = task.result
                    if (documents != null && !documents.isEmpty) {
                        // Email already exists in the collection
                        Toast.makeText(context, "User with this email already exists.", Toast.LENGTH_SHORT).show()
                    } else {
                        // Email does not exist, add the new user
                        addUserToDatabase(userData)
                    }
                } else {
                    Toast.makeText(context, "Error checking email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Adds the user data to the Firestore 'users' collection.
     * @param userData A map containing the user details.
     */
    private fun addUserToDatabase(userData: Map<String, Any>) {
        db.collection("users")
            .add(userData)
            .addOnSuccessListener {
                Toast.makeText(context, "User added successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
