package com.bou.bouvoice.ui.vote

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import com.bou.bouvoice.R
import com.bou.bouvoice.ui.vote.NomineeVotingActivity // Make sure to import the correct activity for voting

class VoteInstructionsDialog(private val context: Context) {

    fun showDialog() {
        // Inflate the dialog view from dialog_vote_instructions.xml
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.dialog_vote_instructions, null)

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        // Set up the Proceed button behavior
        val proceedButton: Button = dialogView.findViewById(R.id.vote_proceed_button)
        proceedButton.setOnClickListener {
            dialog.dismiss()  // Close the dialog

            // Directly navigate to NomineeVotingActivity
            val intent = Intent(context, NomineeVotingActivity::class.java)
            context.startActivity(intent) // Start the NomineeVotingActivity
        }

        // Set up the Close button behavior
        val closeButton: Button = dialogView.findViewById(R.id.vote_close_button)
        closeButton.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        // Display the dialog
        dialog.show()
    }
}
