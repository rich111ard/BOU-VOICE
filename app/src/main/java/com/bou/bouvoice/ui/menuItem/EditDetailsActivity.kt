package com.bou.bouvoice.ui.menuItem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bou.bouvoice.R
import com.bou.bouvoice.ui.menuItem.ChangeFullNameDialog
import com.bou.bouvoice.ui.menuItem.ChangeProfilePictureDialog
import com.bou.bouvoice.ui.menuItem.ChangePasswordDialog

class EditDetailsActivity : AppCompatActivity() {

    private lateinit var profilePictureDialog: ChangeProfilePictureDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_details)

        findViewById<Button>(R.id.change_full_name_button).setOnClickListener {
            ChangeFullNameDialog(this).show()
        }

        findViewById<Button>(R.id.change_profile_picture_button).setOnClickListener {
            // Pass both context and parentActivity
            profilePictureDialog = ChangeProfilePictureDialog(this, this)
            profilePictureDialog.show()
        }

        findViewById<Button>(R.id.change_password_button).setOnClickListener {
            ChangePasswordDialog(this).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Delegate the result to the dialog
        profilePictureDialog.handleActivityResult(requestCode, resultCode, data)
    }
}
