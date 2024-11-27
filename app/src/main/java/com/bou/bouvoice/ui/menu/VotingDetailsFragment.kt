package com.bou.bouvoice.ui.menu

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class VotingDetailsFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var departmentSpinner: Spinner
    private lateinit var roleEditText: EditText
    private lateinit var proceedButton: Button
    private var imageUri: Uri? = null  // Initialized to null to track the uploaded image

    private val nomineeViewModel: NomineeViewModel by activityViewModels()

    // To handle image upload result
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_voting_details, container, false)

        // Initialize views
        imageView = view.findViewById(R.id.imageView_uploaded_image)
        uploadButton = view.findViewById(R.id.button_upload_image)
        nameEditText = view.findViewById(R.id.input_name)
        departmentSpinner = view.findViewById(R.id.spinner_department)
        roleEditText = view.findViewById(R.id.input_role)
        proceedButton = view.findViewById(R.id.button_proceed)

        // Set up the Spinner with department array
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.department_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            departmentSpinner.adapter = adapter
        }

        // Set up image picker
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    imageView.setImageURI(it)  // Show the selected image
                }
            }
        }

        // Handle the upload image button click
        uploadButton.setOnClickListener {
            pickImageFromGallery()
        }

        // Handle the proceed button click
        proceedButton.setOnClickListener {
            handleFormSubmission()
        }

        return view
    }

    // Function to trigger image picker
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    // Function to handle form submission
    private fun handleFormSubmission() {
        val name = nameEditText.text.toString().trim()
        val department = departmentSpinner.selectedItem.toString()
        val role = roleEditText.text.toString().trim()

        // Check if all fields are filled in
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }

        if (role.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter the role you are contesting for", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please upload an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Add the nominee to the ViewModel
        val nominee = Nominee(name, department, role, imageUri!!)
        nomineeViewModel.addNominee(nominee)

        // Navigate to the Nominee List Fragment
        findNavController().navigate(R.id.action_votingDetailsFragment_to_nomineeListFragment)
    }
}
