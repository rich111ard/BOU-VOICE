package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class ElectionSetupFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_election_setup, container, false)

        // Initialize buttons
        val departmentVotingButton: Button = view.findViewById(R.id.department_voting_button)
        val bankWideVotingButton: Button = view.findViewById(R.id.bank_wide_voting_button)
        val othersButton: Button = view.findViewById(R.id.others_button)

        // Set up navigation when buttons are clicked
        departmentVotingButton.setOnClickListener {
            findNavController().navigate(R.id.action_electionSetupFragment_to_votingDetailsFragment)
        }

        bankWideVotingButton.setOnClickListener {
            findNavController().navigate(R.id.action_electionSetupFragment_to_votingDetailsFragment)
        }

        othersButton.setOnClickListener {
            findNavController().navigate(R.id.action_electionSetupFragment_to_votingDetailsFragment)
        }

        return view
    }
}
