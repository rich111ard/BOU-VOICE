package com.bou.bouvoice.ui.bank_wide

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bou.bouvoice.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class BankWideFragment : Fragment() {

    private lateinit var suggestionsContainer: ViewGroup
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bank_wide, container, false)
        suggestionsContainer = view.findViewById(R.id.suggestions_container)
        fetchSuggestions()
        return view
    }

    private fun fetchSuggestions() {
        db.collection("suggestions")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val department = document.getString("endDepartment") ?: "Unknown Department"
                    val content = document.getString("content") ?: "No Content"
                    val status = document.getBoolean("status") ?: false
                    val agrees = document.getLong("agrees") ?: 0
                    val createdAt = document.getTimestamp("createdAt")?.toDate()

                    val formattedDate = createdAt?.let {
                        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
                    } ?: "Unknown Date"

                    addSuggestionCard(department, content, agrees.toInt(), status, formattedDate)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch suggestions", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addSuggestionCard(
        department: String,
        content: String,
        agrees: Int,
        status: Boolean,
        createdAt: String
    ) {
        val cardView = LayoutInflater.from(context)
            .inflate(R.layout.suggestion_card_layout, suggestionsContainer, false) as ConstraintLayout

        val departmentView = cardView.findViewById<TextView>(R.id.tv_department_name)
        val contentView = cardView.findViewById<TextView>(R.id.tv_suggestion_content)
        val statusView = cardView.findViewById<TextView>(R.id.tv_status)
        val agreesView = cardView.findViewById<TextView>(R.id.tv_agree_count)
        val agreeButton = cardView.findViewById<Button>(R.id.btn_agree)
        val dateView = cardView.findViewById<TextView>(R.id.tv_date_time)

        departmentView.text = department
        contentView.text = content
        agreesView.text = "$agrees agrees"
        dateView.text = createdAt

        // Dynamic styling for status
        val statusText = if (status) "STATUS: Approved" else "STATUS: Pending"
        val spannable = SpannableString(statusText)

        // Apply blue color and bold style to "STATUS:"
        val statusLabel = "STATUS:"
        val
        ::contentReference[oaicite:0]{index=0}

