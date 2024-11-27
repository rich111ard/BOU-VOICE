package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class ElectionSuccessFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_election_success, container, false)

        val publicCheckBox: CheckBox = view.findViewById(R.id.public_checkbox)
        val privateCheckBox: CheckBox = view.findViewById(R.id.private_checkbox)
        val confirmButton: Button = view.findViewById(R.id.button_confirm)

        // Ensure only one checkbox is checked at a time
        publicCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                privateCheckBox.isChecked = false
            }
        }

        privateCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                publicCheckBox.isChecked = false
            }
        }

        // Handle the confirm button click
        confirmButton.setOnClickListener {
            if (!publicCheckBox.isChecked && !privateCheckBox.isChecked) {
                Toast.makeText(requireContext(), "Please select either public or private", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Election setup successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_electionSuccessFragment_to_electionSetupFragment)
            }
        }

        return view
    }
}
