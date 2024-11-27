package com.bou.bouvoice.ui.departs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentSuccessBinding

class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set heading text for submission success
        binding.submissionSuccessHeading.text = "Submission Successful!"

        // Update the success message to be general
        binding.successMessage.text = """
            Thank you for your valuable input! 
            
            Your feedback has been successfully submitted. We truly appreciate your time and effort in helping us improve our services. 
            
            Every suggestion counts and will contribute to a better and more efficient process.
            
            We want you to know that your suggestions are not just heard but are acted upon. Together, we can build a more productive environment and enhance our commitment to excellence.
            
            Please keep sharing your insights – your voice matters!
        """.trimIndent()

        // Handle "Go to Home" button click
        binding.goHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_successFragment_to_welcomeSuggestFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
