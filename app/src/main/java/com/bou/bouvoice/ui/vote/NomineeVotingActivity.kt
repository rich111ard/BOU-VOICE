package com.bou.bouvoice.ui.vote

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.R
import android.graphics.Color


class NomineeVotingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nominee_voting)

        // Vote buttons
        val voteNominee1Button: Button = findViewById(R.id.vote_nominee_1)
        val voteNominee2Button: Button = findViewById(R.id.vote_nominee_2)

        // Submit button
        val submitVotesButton: Button = findViewById(R.id.submit_votes_button)

        // Click listeners for vote buttons
        voteNominee1Button.setOnClickListener {
            handleVoteClick(voteNominee1Button)
        }

        voteNominee2Button.setOnClickListener {
            handleVoteClick(voteNominee2Button)
        }

        // Click listener for the submit button
        submitVotesButton.setOnClickListener {
            showThankYouDialog()
        }
    }

    // Function to handle the vote button click
    private fun handleVoteClick(button: Button) {
        button.apply {
            isClickable = false // Disable further clicks
            setBackgroundColor(Color.TRANSPARENT) // Set background to colorless (fully transparent)
        }
        Toast.makeText(this, "Vote casted", Toast.LENGTH_SHORT).show() // Show toast message
    }

    // Function to show thank you dialog
    private fun showThankYouDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Thank you for your participation!")
            .setCancelable(false)

        val dialog = builder.create()
        dialog.show()

        // Close the dialog after 3 seconds and redirect
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            finish() // Closes this activity and goes back to the previous activity (Bank Wide)
        }, 3000) // 3-second delay
    }
}
