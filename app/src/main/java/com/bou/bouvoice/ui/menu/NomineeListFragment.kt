package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R

class NomineeListFragment : Fragment() {

    private val nomineeViewModel: NomineeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nominee_list, container, false)

        val nomineeContainer: LinearLayout = view.findViewById(R.id.nominee_container)
        val addNomineeButton: Button = view.findViewById(R.id.button_add_nominee)
        val proceedButton: Button = view.findViewById(R.id.button_proceed)

        // Add nominee button logic
        addNomineeButton.setOnClickListener {
            findNavController().navigate(R.id.action_nomineeListFragment_to_votingDetailsFragment)
        }

        // Proceed button logic
        proceedButton.setOnClickListener {
            if (nomineeViewModel.nominees.value?.size ?: 0 < 2) {
                Toast.makeText(requireContext(), "Please add at least two nominees", Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_nomineeListFragment_to_choosePeopleFragment)
            }
        }

        // Observe nominee list and dynamically update UI
        nomineeViewModel.nominees.observe(viewLifecycleOwner) { nominees: List<Nominee> ->
            nomineeContainer.removeAllViews()

            nominees.forEachIndexed { index, nominee ->
                val nomineeView = layoutInflater.inflate(R.layout.nominee_item, nomineeContainer, false)

                val nameTextView: TextView = nomineeView.findViewById(R.id.nominee_name)
                val departmentTextView: TextView = nomineeView.findViewById(R.id.nominee_department)
                val roleTextView: TextView = nomineeView.findViewById(R.id.nominee_role)
                val nomineeImageView: ImageView = nomineeView.findViewById(R.id.nominee_image)
                val removeButton: Button = nomineeView.findViewById(R.id.button_remove_nominee)

                nameTextView.text = nominee.name
                departmentTextView.text = nominee.department
                roleTextView.text = nominee.role
                nominee.imageUri?.let { uri -> nomineeImageView.setImageURI(uri) }

                // Remove nominee logic
                removeButton.setOnClickListener {
                    nomineeViewModel.removeNominee(index)
                }

                nomineeContainer.addView(nomineeView)
            }
        }

        return view
    }
}
