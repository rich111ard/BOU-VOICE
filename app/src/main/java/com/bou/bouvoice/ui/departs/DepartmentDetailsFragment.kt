package com.bou.bouvoice.ui.departs

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentDepartmentDetailsBinding
import com.bou.bouvoice.ui.utils.FirestoreUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DepartmentDetailsFragment : Fragment() {

    private var _binding: FragmentDepartmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val sectionList = mutableListOf<Pair<String, Boolean>>()
    private val prioritizedSections = mutableListOf<Pair<String, Boolean>>()
    private var isBankWide = false
    private var progressDialog: ProgressDialog? = null
    private var selectedAdminRole: String? = null
    private var selectedAdminEmail: String? = null
    private var selectedDivision: String? = null // Holds the checked division

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDepartmentDetailsBinding.inflate(inflater, container, false)

        val departmentName = arguments?.getString("departmentName") ?: "Unknown Department"
        val suggestionContent = arguments?.getString("suggestionContent") ?: ""
        val isPrivate = arguments?.getBoolean("isPrivate") ?: false

        binding.tvDepartmentTitle.text = departmentName

        Log.d("DepartmentDetailsFragment", "Suggestion Content: $suggestionContent")
        Log.d("DepartmentDetailsFragment", "Is Private: $isPrivate")

        setupToWhomItConcernsCheckbox(departmentName)

        binding.btnSubmit.setOnClickListener {
            submitSuggestion(departmentName, suggestionContent, isPrivate)
        }

        showProgressDialog("Loading sections...")
        if (departmentName == "Bank-wide") {
            isBankWide = true
            fetchSectionsAndUsers("Human Resource") // Load HR sections for Bank-wide
        } else {
            fetchSectionsAndUsers(departmentName)
        }

        return binding.root
    }

    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(requireContext()).apply {
            setMessage(message)
            setCancelable(false)
            show()
        }
    }

    private fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    private fun setupToWhomItConcernsCheckbox(departmentName: String) {
        binding.checkboxToWhomItConcerns.isChecked = true
        binding.checkboxToWhomItConcerns.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                uncheckOtherCheckboxes()
                fetchDeputyDirectorDetails(departmentName) { email, division ->
                    selectedAdminEmail = email ?: "Unknown"
                    selectedAdminRole = "Deputy Director"
                    selectedDivision = division ?: "General"
                }
            }
        }
    }

    private fun fetchDeputyDirectorDetails(departmentName: String, callback: (String?, String?) -> Unit) {
        db.collection("users")
            .whereEqualTo("department", departmentName)
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
                Log.e("DepartmentDetailsFragment", "Error fetching Deputy Director details: ${exception.message}")
                callback(null, null)
            }
    }

    private fun uncheckOtherCheckboxes() {
        for (i in 0 until binding.sectionCheckboxContainer.childCount) {
            val checkBox = binding.sectionCheckboxContainer.getChildAt(i) as CheckBox
            checkBox.isChecked = false
        }
    }

    private fun fetchSectionsAndUsers(departmentName: String) {
        binding.sectionCheckboxContainer.removeAllViews()
        sectionList.clear()
        prioritizedSections.clear()

        val departmentId = departmentName.lowercase().replace(" ", "_").replace("-", "_")
        db.collection("departments").document(departmentId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val sections = document.get("sections") as? List<String> ?: emptyList()
                    if (sections.isNotEmpty()) {
                        sections.forEach { section ->
                            fetchUserForSection(section, departmentName)
                        }
                    } else {
                        showErrorMessage("No divisions found for this department.")
                    }
                } else {
                    showErrorMessage("Department not found.")
                }
                hideProgressDialog()
            }
            .addOnFailureListener {
                hideProgressDialog()
                showErrorMessage("Failed to load divisions. Please check your internet connection.")
            }
    }

    private fun fetchUserForSection(section: String, departmentName: String) {
        db.collection("users")
            .whereEqualTo("division", section)
            .whereEqualTo("department", departmentName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    querySnapshot.documents.forEach { document ->
                        val adminRole = document.getString("adminRole") ?: "None"
                        if (adminRole == "None") return@forEach

                        val fullName = document.getString("fullName") ?: "No user"
                        val isNoUser = fullName == "No user"
                        val sectionInfo = "$adminRole: $section ($fullName)"

                        if (adminRole == "Deputy Director" || adminRole == "Director") {
                            prioritizedSections.add(Pair(sectionInfo, isNoUser))
                        } else {
                            sectionList.add(Pair(sectionInfo, isNoUser))
                        }
                    }
                    updateSectionListUI()
                }
            }
            .addOnFailureListener {
                hideProgressDialog()
                showErrorMessage("Failed to load user for $section. Check your internet connection.")
            }
    }

    private fun submitSuggestion(departmentName: String, suggestionContent: String, isPrivate: Boolean) {
        if (suggestionContent.isEmpty()) {
            Toast.makeText(requireContext(), "No suggestion content provided.", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Anonymous"

        showProgressDialog("Submitting suggestion...")

        FirestoreUtils.fetchAndIncrementUserId(userEmail) { uniqueUserId: String ->
            db.collection("users")
                .whereEqualTo("adminRole", selectedAdminRole)
                .whereEqualTo("department", departmentName)
                .whereEqualTo("division", selectedDivision)
                .get()
                .addOnSuccessListener { snapshot ->
                    val adminDocument = snapshot.documents.firstOrNull()
                    val address = adminDocument?.getString("email") ?: "Unknown"

                    val endDivision = selectedDivision ?: "Unknown"
                    val endAdminRole = selectedAdminRole ?: "Unknown"

                    db.collection("users").document(userEmail).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                val userDepartment = document.getString("department") ?: "Unknown Department"
                                val userDivision = document.getString("division") ?: "Unknown Division"

                                val suggestion = hashMapOf(
                                    "content" to suggestionContent,
                                    "userID" to uniqueUserId,
                                    "department" to userDepartment,
                                    "division" to userDivision,
                                    "isPrivate" to isPrivate,
                                    "createdAt" to Timestamp.now(),
                                    "agrees" to 0,
                                    "address" to address, // Fetched admin email
                                    "status" to false,
                                    "endDepartment" to departmentName,
                                    "endAdminRole" to endAdminRole,
                                    "endDivision" to endDivision
                                )

                                db.collection("suggestions").document(uniqueUserId).set(suggestion)
                                    .addOnSuccessListener {
                                        hideProgressDialog()
                                        Toast.makeText(requireContext(), "Suggestion submitted successfully!", Toast.LENGTH_SHORT).show()
                                        findNavController().navigate(R.id.action_departmentDetailsFragment_to_successFragment)
                                    }
                                    .addOnFailureListener { e ->
                                        hideProgressDialog()
                                        Log.e("DepartmentDetailsFragment", "Error submitting suggestion: ${e.message}")
                                        Toast.makeText(requireContext(), "Error submitting suggestion.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                hideProgressDialog()
                                Toast.makeText(requireContext(), "User details not found in Firestore.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { e ->
                            hideProgressDialog()
                            Log.e("DepartmentDetailsFragment", "Error fetching user data: ${e.message}")
                            Toast.makeText(requireContext(), "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    hideProgressDialog()
                    Log.e("DepartmentDetailsFragment", "Error fetching admin data: ${e.message}")
                    Toast.makeText(requireContext(), "Error fetching admin details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showErrorMessage(message: String) {
        hideProgressDialog()
        binding.errorMessage.text = message
        binding.errorMessage.isVisible = true
    }

    private fun updateSectionListUI() {
        binding.sectionCheckboxContainer.removeAllViews()
        sectionList.forEach { addSectionCheckbox(it.first, it.second) }
        prioritizedSections.forEach { addSectionCheckbox(it.first, it.second) }

        if (sectionList.isEmpty() && prioritizedSections.isEmpty()) {
            showErrorMessage("No divisions found for this department.")
        }
    }

    private fun addSectionCheckbox(sectionInfo: String, isNoUser: Boolean) {
        val checkBox = CheckBox(requireContext()).apply {
            text = sectionInfo
            textSize = 16f
            if (isNoUser) {
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
                setTypeface(typeface, android.graphics.Typeface.ITALIC)
            }
            setOnCheckedChangeListener { _, isChecked ->
                val safeText = text.toString()
                if (isChecked) {
                    if (isBankWide) {
                        Toast.makeText(
                            requireContext(),
                            "Only 'To Whom It Concerns' is allowed for Bank-wide suggestions.",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.checkboxToWhomItConcerns.isChecked = true
                        this.isChecked = false
                    } else {
                        binding.checkboxToWhomItConcerns.isChecked = false
                        uncheckOtherCheckboxesExcept(this)

                        selectedAdminRole = safeText.substringBefore(":").trim()
                        selectedDivision = safeText.substringAfter(": ").substringBefore(" (").trim()
                        selectedAdminEmail = safeText.substringAfter("(").substringBefore(")").trim()
                    }
                }
            }
        }
        binding.sectionCheckboxContainer.addView(checkBox)
    }

    private fun uncheckOtherCheckboxesExcept(exceptCheckbox: CheckBox) {
        for (i in 0 until binding.sectionCheckboxContainer.childCount) {
            val checkBox = binding.sectionCheckboxContainer.getChildAt(i) as CheckBox
            if (checkBox != exceptCheckbox) checkBox.isChecked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
