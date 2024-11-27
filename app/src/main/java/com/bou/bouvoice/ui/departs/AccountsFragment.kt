package com.bou.bouvoice.ui.departs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentAccountsBinding
import com.bou.bouvoice.ui.suggest.SuggestViewModel

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    // ViewModel to retrieve the suggestion input from SuggestFragment
    private val viewModel: SuggestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)

        // Group all checkboxes into a list
        val checkboxes = listOf(
            binding.teamLeader1Checkbox,
            binding.teamLeader2Checkbox,
            binding.teamLeader3Checkbox,
            binding.teamLeader4Checkbox,
            binding.director1Checkbox,
            binding.director2Checkbox,
            binding.toWhomItConcernsCheckbox
        )

        // Add logic to uncheck other checkboxes when one is checked
        for (checkbox in checkboxes) {
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkboxes.filter { it != checkbox }.forEach { it.isChecked = false }
                }
            }
        }

        // Handle Go Back Home button click
        binding.goBackHomeButton.setOnClickListener {
            // Navigate back to SuggestFragment without clearing the text
            findNavController().navigate(R.id.action_accountsFragment_to_suggestFragment)
        }

        // Handle Submit Button click
        binding.submitSuggestionButton.setOnClickListener {
            if (validateCheckboxSelection()) {
                // No need to check for empty suggestion since user reached this fragment after entering text
                // Directly submit and navigate to success page
                submitSuggestionAndNavigateToSuccess()
            } else {
                // Show a message that at least one checkbox must be selected
                Toast.makeText(requireContext(), "Please select at least one option before submitting.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    // Method to submit the suggestion and navigate to the success page
    private fun submitSuggestionAndNavigateToSuccess() {
        // Clear suggestion after successful submission in the ViewModel
        viewModel.clearSuggestionText()

        // Navigate to the success page
        findNavController().navigate(R.id.action_accountsFragment_to_successFragment)
    }

    // Checkbox validation
    private fun validateCheckboxSelection(): Boolean {
        val isAnyTeamLeaderChecked = binding.teamLeader1Checkbox.isChecked ||
                binding.teamLeader2Checkbox.isChecked ||
                binding.teamLeader3Checkbox.isChecked ||
                binding.teamLeader4Checkbox.isChecked

        val isAnyDirectorChecked = binding.director1Checkbox.isChecked ||
                binding.director2Checkbox.isChecked

        // Return true if at least one checkbox is selected, or "To whom it concerns" is checked
        return binding.toWhomItConcernsCheckbox.isChecked || isAnyTeamLeaderChecked || isAnyDirectorChecked
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
