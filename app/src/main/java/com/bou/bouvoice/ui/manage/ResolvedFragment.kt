package com.bou.bouvoice.ui.manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.bou.bouvoice.R
import androidx.core.content.ContextCompat

class ResolvedFragment : Fragment() {

    private lateinit var buttonPending: Button
    private lateinit var buttonImplemented: Button
    private lateinit var contentLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_resolved, container, false)

        // Initialize views
        buttonPending = view.findViewById(R.id.button_pending)
        buttonImplemented = view.findViewById(R.id.button_implemented)
        contentLayout = view.findViewById(R.id.content_layout)

        // Set click listeners for Pending and Implemented buttons
        buttonPending.setOnClickListener {
            loadPendingSuggestions()
            updateButtonStyles(buttonPending, buttonImplemented)
        }

        buttonImplemented.setOnClickListener {
            loadImplementedSuggestions()
            updateButtonStyles(buttonImplemented, buttonPending)
        }

        // Load pending suggestions by default
        loadPendingSuggestions()
        updateButtonStyles(buttonPending, buttonImplemented)

        return view
    }

    // Function to load Pending suggestions dynamically
    private fun loadPendingSuggestions() {
        contentLayout.removeAllViews() // Clear current content

        val suggestions = listOf(
            Pair("Human Resources", "We recommend introducing more flexible work hours to improve work-life balance and boost employee morale."),
            Pair("IT Operations & Infrastructure", "There is a need to upgrade the internal network for faster and more secure remote access for employees working from home."),
            Pair("Accounts", "Automating the month-end financial closing process could help reduce delays and improve accuracy."),
            Pair("Communications", "Launch a new internal newsletter to keep employees informed about new policies and developments."),
            Pair("Strategy & Quality Assurance", "Conduct bi-annual strategy workshops to engage employees in refining organizational goals."),
            Pair("Currency", "Introduce digital tools to help improve the currency distribution tracking system, minimizing delays.")
        )

        for ((department, suggestion) in suggestions) {
            val suggestionView = layoutInflater.inflate(R.layout.suggestion_pending_layout, contentLayout, false)
            suggestionView.findViewById<TextView>(R.id.department_name).text = department
            suggestionView.findViewById<TextView>(R.id.suggestion_content).text = suggestion
            contentLayout.addView(suggestionView)
        }
    }

    // Function to load Implemented suggestions dynamically
    private fun loadImplementedSuggestions() {
        contentLayout.removeAllViews() // Clear current content

        val suggestions = listOf(
            Pair("Human Resources", "Develop a more comprehensive employee benefits program, including mental health support."),
            Pair("IT Operations & Infrastructure", "Improve cyber security protocols by regularly updating security policies and conducting regular audits."),
            Pair("Accounts", "Implement a real-time financial dashboard for department heads to view and manage budgets."),
            Pair("Communications", "Create a centralized platform where employees can submit feedback anonymously."),
            Pair("Risk & Compliance", "Conduct quarterly risk assessments across all departments to ensure compliance with updated regulatory standards."),
            Pair("Financial Stability", "Establish a cross-functional team to monitor and report on macroeconomic risks impacting the financial system.")
        )

        for ((department, suggestion) in suggestions) {
            val suggestionView = layoutInflater.inflate(R.layout.suggestion_implemented_layout, contentLayout, false)
            suggestionView.findViewById<TextView>(R.id.department_name).text = department
            suggestionView.findViewById<TextView>(R.id.suggestion_content).text = suggestion
            contentLayout.addView(suggestionView)
        }
    }

    // Helper function to highlight the active button
    private fun updateButtonStyles(activeButton: Button, inactiveButton: Button) {
        activeButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
        inactiveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }
}
