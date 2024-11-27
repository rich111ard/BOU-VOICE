package com.bou.bouvoice.ui.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.bou.bouvoice.databinding.FragmentManageBinding

class ManageFragment : Fragment() {

    private var _binding: FragmentManageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageBinding.inflate(inflater, container, false)

        // Sample suggestion texts
        val suggestion1 = "We recommend improving the work-life balance programs for employees by introducing flexible work hours and enhancing mental health support programs."
        val suggestion2 = "The remote work infrastructure needs significant improvement. We should invest in more secure VPN services and improve the speed of remote server access."
        val suggestion3 = "We need to streamline financial reporting processes to improve accuracy and reduce delays in month-end closings."

        // Handle Respond button clicks to navigate to RespondFragment
        binding.btnRespond.setOnClickListener {
            findNavController().navigate(R.id.action_manageFragment_to_respondFragment)
        }

        binding.btnRespond2.setOnClickListener {
            findNavController().navigate(R.id.action_manageFragment_to_respondFragment)
        }

        binding.btnRespond3.setOnClickListener {
            findNavController().navigate(R.id.action_manageFragment_to_respondFragment)
        }

        // Handle refer button clicks and pass the corresponding suggestion text
        binding.btnRefer.setOnClickListener {
            val action = ManageFragmentDirections.actionManageFragmentToSuggestFragment(suggestion1)
            findNavController().navigate(action)
        }

        binding.btnRefer2.setOnClickListener {
            val action = ManageFragmentDirections.actionManageFragmentToSuggestFragment(suggestion2)
            findNavController().navigate(action)
        }

        binding.btnRefer3.setOnClickListener {
            val action = ManageFragmentDirections.actionManageFragmentToSuggestFragment(suggestion3)
            findNavController().navigate(action)
        }

        // Set OnClickListener for the Resolved button
        binding.buttonResolved.setOnClickListener {
            findNavController().navigate(R.id.action_manageFragment_to_resolvedFragment)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
