package com.bou.bouvoice.ui.suggest

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentSuggestBinding
import com.bou.bouvoice.ui.utils.FirestoreUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class SuggestFragment : Fragment() {

    private var _binding: FragmentSuggestBinding? = null
    private val binding get() = _binding!!
    private var selectedDepartment: String? = null
    private val viewModel: SuggestViewModel by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private var userDepartment: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuggestBinding.inflate(inflater, container, false)

        // Initialize the progress dialog
        progressDialog = ProgressDialog(requireContext()).apply {
            setMessage("Submitting suggestion...")
            setCancelable(false)
        }

        // Set up text watcher for suggestion input
        binding.inputGeneralSuggestion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSuggestionText(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Fetch user department and setup dropdown
        fetchUserDepartmentAndSetupDropdown()

        // Handle submit button click
        binding.btnSubmitGeneralSuggestion.setOnClickListener {
            handleSubmitSuggestion()
        }

        return binding.root
    }

    private fun handleSubmitSuggestion() {
        val suggestionText = binding.inputGeneralSuggestion.text.toString().trim()
        val isPrivate = binding.privateSuggestionCheckbox.isChecked

        if (suggestionText.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your suggestion before submitting.", Toast.LENGTH_SHORT).show()
            return
        }

        // Show progress dialog
        progressDialog?.show()

        // Fetch the current user's email
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous"

        // Fetch and increment userID
        FirestoreUtils.fetchAndIncrementUserId(userEmail) { uniqueUserId: String ->
            // Fetch Deputy Director of HR details
            fetchDeputyDirectorDetails { email, division ->
                if (email == null || division == null) {
                    progressDialog?.dismiss()
                    Toast.makeText(requireContext(), "Failed to fetch Deputy Director details.", Toast.LENGTH_SHORT).show()
                    return@fetchDeputyDirectorDetails
                }

                // Submit suggestion with fetched details
                submitSuggestion(suggestionText, isPrivate, email, "Human Resource", "Deputy Director", division, uniqueUserId)
            }
        }
    }



    private fun fetchDeputyDirectorDetails(callback: (String?, String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("department", "Human Resource")
            .whereEqualTo("adminRole", "Deputy Director")
            .get()
            .addOnSuccessListener { snapshot ->
                val document = snapshot.documents.firstOrNull()
                if (document != null) {
                    val email = document.getString("email")
                    val division = document.getString("division")
                    callback(email, division)
                } else {
                    callback(null, null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SuggestFragment", "Error fetching Deputy Director: ${exception.message}")
                callback(null, null)
            }
    }

    private fun generateUniqueUserId(callback: (String) -> Unit) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous"
        db.collection("suggestions")
            .whereEqualTo("address", userEmail) // Count suggestions submitted by the user
            .get()
            .addOnSuccessListener { snapshot ->
                val count = snapshot.size() + 1 // Increment based on the count of documents
                val uniqueUserId = "${count}_$userEmail" // Format: number_current user email
                callback(uniqueUserId) // Return the generated unique ID
            }
            .addOnFailureListener { e ->
                Log.e("SuggestFragment", "Error generating unique userID: ${e.message}")
                callback("1_$userEmail") // Fallback to "1_email" if there's an error
            }
    }


    private fun submitSuggestion(
        content: String,
        isPrivate: Boolean,
        address: String,
        endDepartment: String,
        endAdminRole: String,
        endDivision: String,
        userId: String // Pass the unique user ID
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            db.collection("users").document(user.email!!).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userDepartment = document.getString("department") ?: "Unknown Department"
                        val userDivision = document.getString("division") ?: "Unknown Division"

                        // Prepare suggestion data
                        val suggestion = hashMapOf(
                            "content" to content,
                            "userID" to userId, // Use the unique user ID here
                            "department" to userDepartment,
                            "division" to userDivision,
                            "isPrivate" to isPrivate,
                            "createdAt" to Timestamp.now(),
                            "agrees" to 0,
                            "address" to address,
                            "status" to false,
                            "endDepartment" to endDepartment,
                            "endAdminRole" to endAdminRole,
                            "endDivision" to endDivision
                        )

                        // Add suggestion to Firestore using userId as the document ID
                        db.collection("suggestions").document(userId).set(suggestion)
                            .addOnSuccessListener {
                                progressDialog?.dismiss()
                                Toast.makeText(requireContext(), "Suggestion submitted successfully!", Toast.LENGTH_SHORT).show()
                                resetForm()
                                findNavController().navigate(R.id.action_suggestFragment_to_successFragment)
                            }
                            .addOnFailureListener { e ->
                                progressDialog?.dismiss()
                                Log.e("SuggestFragment", "Error submitting suggestion: ${e.message}")
                                Toast.makeText(requireContext(), "Error submitting suggestion: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        progressDialog?.dismiss()
                        Toast.makeText(requireContext(), "User details not found in Firestore.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog?.dismiss()
                    Log.e("SuggestFragment", "Error fetching user data: ${e.message}")
                    Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            progressDialog?.dismiss()
            Toast.makeText(requireContext(), "User is not logged in.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun resetForm() {
        binding.inputGeneralSuggestion.text.clear()
        binding.privateSuggestionCheckbox.isChecked = false
    }

    private fun fetchUserDepartmentAndSetupDropdown() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            db.collection("users").document(user.email!!).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userDepartment = document.getString("department") ?: "Unknown Department"
                        Log.d("SuggestFragment", "User department: $userDepartment")
                        fetchDepartmentsFromDatabase()
                    } else {
                        Toast.makeText(requireContext(), "User data not found in Firestore.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to retrieve user department: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun fetchDepartmentsFromDatabase() {
        db.collection("departments").get()
            .addOnSuccessListener { documents ->
                val departmentsList = mutableListOf("CHOOSE DEPARTMENT")
                userDepartment?.let { departmentsList.add(it) }
                departmentsList.add("Bank-wide")
                departmentsList.add("OTHERS")

                val sortedDepartments = documents.map { it.getString("departmentName") ?: "" }.sorted()
                departmentsList.addAll(sortedDepartments)

                setupDepartmentDropdownAdapter(departmentsList)
            }
            .addOnFailureListener {
                setupDepartmentDropdownAdapter(listOf("CHOOSE DEPARTMENT", "Unable to load departments"))
                Toast.makeText(requireContext(), "Failed to load departments. Please check your connection.", Toast.LENGTH_SHORT).show()
            }

        binding.departmentDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedText = parent.getItemAtPosition(position).toString()
                val suggestionText = binding.inputGeneralSuggestion.text.toString().trim()

                if (suggestionText.isEmpty() && position != 0) {
                    Toast.makeText(requireContext(), "Please enter a suggestion before selecting a department.", Toast.LENGTH_SHORT).show()
                    binding.departmentDropdown.setSelection(0)
                    return
                }

                if (position != 0 && selectedText != "Unable to load departments" && selectedText != "OTHERS") {
                    navigateToDepartmentDetails(selectedText)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun setupDepartmentDropdownAdapter(departmentList: List<String>) {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            departmentList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                styleDropdownItem(textView, position)
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                styleDropdownItem(textView, position)
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0 && departmentList[position] != "OTHERS" && departmentList[position] != "Unable to load departments"
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.departmentDropdown.adapter = adapter
    }

    private fun styleDropdownItem(textView: TextView, position: Int) {
        val itemText = binding.departmentDropdown.adapter.getItem(position) as String
        when (itemText) {
            "CHOOSE DEPARTMENT", "OTHERS" -> {
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
                textView.textSize = 18f
                textView.setTypeface(textView.typeface, android.graphics.Typeface.BOLD)
            }
            "Unable to load departments" -> {
                textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                textView.textSize = 16f
                textView.setTypeface(textView.typeface, android.graphics.Typeface.ITALIC)
            }
            else -> {
                textView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                textView.textSize = 16f
                textView.setTypeface(textView.typeface, android.graphics.Typeface.NORMAL)
            }
        }
    }

    private fun navigateToDepartmentDetails(departmentName: String) {
        val bundle = Bundle().apply {
            putString("departmentName", departmentName)
            putString("suggestionContent", binding.inputGeneralSuggestion.text.toString().trim())
            putBoolean("isPrivate", binding.privateSuggestionCheckbox.isChecked)
        }
        findNavController().navigate(R.id.departmentDetailsFragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        progressDialog?.dismiss()
        progressDialog = null
    }
}
