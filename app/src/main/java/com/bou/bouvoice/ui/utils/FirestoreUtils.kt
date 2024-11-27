package com.bou.bouvoice.ui.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreUtils {
    fun fetchAndIncrementUserId(email: String, callback: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Query the suggestions collection to find the highest number for the user
        db.collection("suggestions")
            .whereGreaterThanOrEqualTo("userID", "1_$email") // Filter by the user's email
            .whereLessThanOrEqualTo("userID", "99999_$email") // Ensure we only match this user's IDs
            .get()
            .addOnSuccessListener { snapshot ->
                // Extract the highest number prefix from all matching documents
                val maxNumber = snapshot.documents
                    .mapNotNull { it.id.split("_").firstOrNull()?.toIntOrNull() }
                    .maxOrNull() ?: 0

                // Increment the number for the next ID
                val nextUserId = "${maxNumber + 1}_$email"
                callback(nextUserId)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreUtils", "Error fetching user ID: ${e.message}")
                // Default to 1 if fetching fails
                callback("1_$email")
            }
    }
}
