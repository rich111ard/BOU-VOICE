package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class FinalVotingPageFragment : Fragment() {

    // Using activityViewModels to share data between fragments
    private val specificPeopleViewModel: SpecificPeopleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_final_voting_page, container, false)

        // Initialize UI components
        val nomineeListContainer: LinearLayout = view.findViewById(R.id.nominee_list_container)
        val votersListContainer: LinearLayout = view.findViewById(R.id.voters_list_container)
        val editStaffButton: Button = view.findViewById(R.id.button_edit_staff)
        val editNomineesButton: Button = view.findViewById(R.id.button_edit_nominees)
        val submitElectionButton: Button = view.findViewById(R.id.button_submit_election)
        val checkboxConfirmNominees: CheckBox = view.findViewById(R.id.checkbox_confirm_nominees)
        val checkboxConfirmVoters: CheckBox = view.findViewById(R.id.checkbox_confirm_voters)

        // Observe nominees data from ViewModel
        specificPeopleViewModel.nominees.observe(viewLifecycleOwner) { nominees ->
            nomineeListContainer.removeAllViews()
            nominees.forEach { nominee ->
                val nomineeView = layoutInflater.inflate(R.layout.person_item, nomineeListContainer, false)
                val nomineeTextView: TextView = nomineeView.findViewById(R.id.person_name)
                nomineeTextView.text = nominee
                nomineeListContainer.addView(nomineeView)
            }
        }

        // Observe voters data from ViewModel
        specificPeopleViewModel.voters.observe(viewLifecycleOwner) { voters ->
            votersListContainer.removeAllViews()
            voters.forEach { voter ->
                val voterView = layoutInflater.inflate(R.layout.person_item, votersListContainer, false)
                val voterTextView: TextView = voterView.findViewById(R.id.person_name)
                voterTextView.text = voter
                votersListContainer.addView(voterView)
            }
        }

        // Set button actions
        editStaffButton.setOnClickListener {
            findNavController().navigate(R.id.action_finalVotingPageFragment_to_specificPeopleFragment)
        }

        editNomineesButton.setOnClickListener {
            findNavController().navigate(R.id.action_finalVotingPageFragment_to_nomineeListFragment)
        }

        submitElectionButton.setOnClickListener {
            // Ensure both checkboxes are selected before proceeding
            if (checkboxConfirmNominees.isChecked && checkboxConfirmVoters.isChecked) {
                findNavController().navigate(R.id.action_finalVotingPageFragment_to_electionSuccessFragment)
            } else {
                // Show a toast if both checkboxes are not checked
                Toast.makeText(requireContext(), "Please confirm both the nominee and voter lists", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
