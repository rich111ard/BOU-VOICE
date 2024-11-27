package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class ChoosePeopleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_choose_people, container, false)

        // Button to choose specific people
        val choosePeopleButton: Button = view.findViewById(R.id.button_choose_specific_people)
        choosePeopleButton.setOnClickListener {
            findNavController().navigate(R.id.action_choosePeopleFragment_to_specificPeopleFragment)
        }

        // Proceed button logic
        val proceedButton: Button = view.findViewById(R.id.button_proceed)
        proceedButton.setOnClickListener {
            findNavController().navigate(R.id.action_choosePeopleFragment_to_finalVotingPageFragment)
        }

        return view
    }
}
