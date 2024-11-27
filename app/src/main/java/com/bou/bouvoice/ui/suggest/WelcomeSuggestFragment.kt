package com.bou.bouvoice.ui.suggest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentWelcomeSuggestBinding

class WelcomeSuggestFragment : Fragment() {

    // Declare the binding variable
    private var _binding: FragmentWelcomeSuggestBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using view binding
        _binding = FragmentWelcomeSuggestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set onClickListener for the Proceed button using view binding
        binding.btnProceedToSuggest.setOnClickListener {
            // Navigate to SuggestFragment
            findNavController().navigate(R.id.action_welcomeSuggestFragment_to_suggestFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
