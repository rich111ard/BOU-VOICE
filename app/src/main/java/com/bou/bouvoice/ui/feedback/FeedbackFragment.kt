package com.bou.bouvoice.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentFeedbackBinding

class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!

    // Store the selected button to track which section is active
    private lateinit var selectedButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Default: Show bank-wide feedback when the page is loaded
        selectedButton = binding.bankwideFeedbackButton
        showBankWideFeedback()

        // Handle Departmental Feedback Button click
        binding.departmentFeedbackButton.setOnClickListener {
            changeButtonSelection(binding.departmentFeedbackButton)
            showDepartmentFeedback()
        }

        // Handle Bank-wide Feedback Button click
        binding.bankwideFeedbackButton.setOnClickListener {
            changeButtonSelection(binding.bankwideFeedbackButton)
            showBankWideFeedback()
        }

        // Handle Personal Feedback Button click
        binding.privateFeedbackButton.setOnClickListener {
            changeButtonSelection(binding.privateFeedbackButton)
            showPrivateFeedback()
        }

        return root
    }

    // Change button selection and background color to indicate active section
    private fun changeButtonSelection(newButton: Button) {
        // Reset color of the previous button
        selectedButton.setBackgroundResource(R.color.colorPrimary)
        selectedButton.setTextColor(resources.getColor(android.R.color.white))

        // Set the new selected button
        selectedButton = newButton
        selectedButton.setBackgroundResource(R.color.colorAccent)  // Highlight the selected button
        selectedButton.setTextColor(resources.getColor(android.R.color.black))
    }

    // Display Departmental Feedback
    private fun showDepartmentFeedback() {
        // Clear existing views and dynamically add feedback cards for Departmental suggestions
        binding.feedbackContentLayout.removeAllViews()
        val inflater = layoutInflater

        // Example: Populate with Departmental feedback
        val departmentFeedback = listOf(
            FeedbackItem("Information Technology Operations", "Upgrade cloud infrastructure", "We will evaluate the upgrade", "04/10/2024", "Team Leader ITO"),
            FeedbackItem("Information Technology Operations", "Improve internal networking", "Networking upgrades are planned", "05/10/2024", "Team Leader ITO")
        )

        for (feedback in departmentFeedback) {
            val cardView = inflater.inflate(R.layout.feedback_card, null)
            populateFeedbackCard(cardView, feedback)
            binding.feedbackContentLayout.addView(cardView)
        }
    }

    // Display Bank-wide Feedback
    private fun showBankWideFeedback() {
        // Clear existing views and dynamically add feedback cards for Bank-wide suggestions
        binding.feedbackContentLayout.removeAllViews()
        val inflater = layoutInflater

        // Example: Populate with Bank-wide feedback
        val bankWideFeedback = listOf(
            FeedbackItem("Finance Department", "Increase transparency in reports", "We will increase report accessibility", "03/10/2024", "Team Leader Finance"),
            FeedbackItem("HR Department", "Mental health sessions", "Mental health sessions will be offered", "02/10/2024", "Team Leader HR")
        )

        for (feedback in bankWideFeedback) {
            val cardView = inflater.inflate(R.layout.feedback_card, null)
            populateFeedbackCard(cardView, feedback)
            binding.feedbackContentLayout.addView(cardView)
        }
    }

    // Display Personal Feedback
    private fun showPrivateFeedback() {
        // Clear existing views and dynamically add feedback cards for Personal suggestions
        binding.feedbackContentLayout.removeAllViews()
        val inflater = layoutInflater

        // Example: Populate with Personal feedback
        val personalFeedback = listOf(
            FeedbackItem("Personal", "Keep my suggestion private", "Your suggestion is under review", "01/10/2024", "Manager IT")
        )

        for (feedback in personalFeedback) {
            val cardView = inflater.inflate(R.layout.feedback_card, null)
            populateFeedbackCard(cardView, feedback)
            binding.feedbackContentLayout.addView(cardView)
        }
    }

    // Helper function to populate feedback card with data
    private fun populateFeedbackCard(view: View, feedback: FeedbackItem) {
        val departmentText = view.findViewById<TextView>(R.id.feedback_department)
        val suggestionText = view.findViewById<TextView>(R.id.feedback_suggestion)
        val feedbackText = view.findViewById<TextView>(R.id.feedback_response)
        val dateText = view.findViewById<TextView>(R.id.feedback_date)
        val suggestedByText = view.findViewById<TextView>(R.id.suggested_by)

        departmentText.text = "Department: ${feedback.department}"
        suggestionText.text = "Suggestion: ${feedback.suggestion}"
        feedbackText.text = "Feedback: ${feedback.feedback}"
        dateText.text = "Date: ${feedback.date}"
        suggestedByText.text = "Suggested by: ${feedback.suggestedBy}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class to hold feedback item details
    data class FeedbackItem(
        val department: String,
        val suggestion: String,
        val feedback: String,
        val date: String,
        val suggestedBy: String
    )
}
