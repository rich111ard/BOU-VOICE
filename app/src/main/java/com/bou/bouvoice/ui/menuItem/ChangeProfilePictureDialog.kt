package com.bou.bouvoice.ui.menuItem

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bou.bouvoice.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide

class ChangeProfilePictureDialog(context: Context, private val parentActivity: Activity) : Dialog(context) {

    private var selectedImageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var loadingDialog: AlertDialog

    companion object {
        const val IMAGE_PICK_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_change_profile_picture)

        // Set the dialog to use a larger portion of the screen
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference.child("profilePictures")

        // Initialize UI components in the dialog
        profileImageView = findViewById(R.id.profile_picture_preview)!!
        val selectPictureButton = findViewById<Button>(R.id.select_picture_button)!!
        val savePictureButton = findViewById<Button>(R.id.save_picture_button)!!
        val cancelButton = findViewById<Button>(R.id.cancel_button)!!

        // Initialize ProgressDialog as AlertDialog
        loadingDialog = AlertDialog.Builder(context)
            .setMessage("Saving profile picture...")
            .setCancelable(false)
            .create()

        // Load existing profile picture from Firestore
        loadCurrentProfilePicture()

        // Button actions
        selectPictureButton.setOnClickListener { openGalleryForImageSelection() }
        savePictureButton.setOnClickListener { saveProfilePicture() }
        cancelButton.setOnClickListener { dismiss() }
    }

    private fun openGalleryForImageSelection() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        parentActivity.startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    // This function should be called from the parent activity’s onActivityResult
    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            profileImageView.setImageURI(selectedImageUri)
        }
    }

    private fun saveProfilePicture() {
        selectedImageUri?.let { uri ->
            // Show the loading dialog
            loadingDialog.show()

            // Generate a unique file name for each upload
            val uniqueFileName = "${System.currentTimeMillis()}.jpg"
            val filePath = storageReference.child("profilePictures/$uniqueFileName")

            // Upload the file to Firebase Storage
            filePath.putFile(uri)
                .addOnSuccessListener {
                    // Get the download URL of the uploaded image
                    filePath.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Update Firestore with the download URL of the profile picture
                        val userEmail = auth.currentUser?.email ?: return@addOnSuccessListener
                        firestore.collection("users").document(userEmail)
                            .update("profilePictureUrl", downloadUri.toString())
                            .addOnSuccessListener {
                                // Dismiss the loading dialog and notify the user
                                loadingDialog.dismiss()
                                Toast.makeText(context, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            .addOnFailureListener { e ->
                                // Handle failure to update Firestore
                                loadingDialog.dismiss()
                                Toast.makeText(context, "Failed to update profile picture URL: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }.addOnFailureListener { e ->
                        // Handle failure to get the download URL
                        loadingDialog.dismiss()
                        Toast.makeText(context, "Failed to retrieve download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure to upload the image
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            // Notify the user to select an image if none is selected
            Toast.makeText(context, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadCurrentProfilePicture() {
        val userEmail = auth.currentUser?.email ?: return
        firestore.collection("users").document(userEmail).get()
            .addOnSuccessListener { document ->
                val profilePictureUrl = document.getString("profilePictureUrl")
                if (!profilePictureUrl.isNullOrEmpty()) {
                    Glide.with(context)
                        .load(profilePictureUrl)
                        .placeholder(R.drawable.ic_user_placeholder) // Default placeholder
                        .error(R.drawable.ic_user_placeholder) // Error placeholder
                        .into(profileImageView)
                } else {
                    // Set placeholder if no URL is found
                    profileImageView.setImageResource(R.drawable.ic_user_placeholder)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
