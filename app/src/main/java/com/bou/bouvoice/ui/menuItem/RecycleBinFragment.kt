package com.bou.bouvoice.ui.menuItem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bou.bouvoice.R

class RecycleBinFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recycle_bin, container, false)

        // Set up retrieve buttons and their click listeners
        val retrieveButton1: Button = view.findViewById(R.id.btn_retrieve_1)
        retrieveButton1.setOnClickListener {
            // Logic to move suggestion 1 back to the pending section
            Toast.makeText(requireContext(), "Suggestion 1 Retrieved", Toast.LENGTH_SHORT).show()
        }

        val retrieveButton2: Button = view.findViewById(R.id.btn_retrieve_2)
        retrieveButton2.setOnClickListener {
            // Logic to move suggestion 2 back to the pending section
            Toast.makeText(requireContext(), "Suggestion 2 Retrieved", Toast.LENGTH_SHORT).show()
        }

        // Continue setting up other retrieve buttons similarly
        return view
    }
}
