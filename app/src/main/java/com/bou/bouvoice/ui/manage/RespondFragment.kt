package com.bou.bouvoice.ui.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentRespondBinding

class RespondFragment : Fragment() {

    private var _binding: FragmentRespondBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRespondBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get references to the checkboxes
        val checkboxIgnore: CheckBox = binding.ignoreCheckbox
        val checkboxImplement: CheckBox = binding.implementCheckbox
        val checkboxPending: CheckBox = binding.pendingCheckbox

        // Logic to ensure only one checkbox can be selected at a time
        val checkboxes = listOf(checkboxIgnore, checkboxImplement, checkboxPending)

        for (checkbox in checkboxes) {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    // Uncheck all other checkboxes
                    checkboxes.filter { it != checkbox }.forEach { it.isChecked = false }
                }
            }
        }

        // Handle the Submit button click event
        binding.submitResponseButton.setOnClickListener {
            // Check which checkbox is selected
            val selectedCheckbox = when {
                checkboxIgnore.isChecked -> "Ignore"
                checkboxImplement.isChecked -> "To be Implemented"
                checkboxPending.isChecked -> "Pending"
                else -> null
            }

            // Check if a response has been provided and a checkbox is selected
            val responseText = binding.responseInput.text.toString().trim()

            if (selectedCheckbox == null) {
                // No checkbox selected, show error message
                Toast.makeText(requireContext(), "Please select an option before submitting.", Toast.LENGTH_SHORT).show()
            } else if (responseText.isEmpty()) {
                // No response text, show error message
                Toast.makeText(requireContext(), "Please provide a response before submitting.", Toast.LENGTH_SHORT).show()
            } else {
                // Proceed with the submission logic
                Toast.makeText(requireContext(), "Response submitted successfully.", Toast.LENGTH_SHORT).show()
                // Perform submission (e.g., save to database, navigate away, etc.)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
