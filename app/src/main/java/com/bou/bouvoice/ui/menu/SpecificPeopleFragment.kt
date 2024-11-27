package com.bou.bouvoice.ui.menu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bou.bouvoice.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class SpecificPeopleFragment : Fragment() {

    // List of all people for selection
    private val allPeople = listOf(
        "John Okello", "Grace Nakato", "Samuel Kizza", "Alice Kabahweza", "David Mugisha", "Sarah Nabirye",
        "Joseph Katumba", "Mary Nambogo", "Isaac Lule", "Miriam Tumwine", "Peter Byaruhanga", "Esther Namugambe",
        "Robert Okumu", "Rose Katushabe", "James Sekandi", "Harriet Kyomugisha", "Simon Wanyama", "Agnes Nabukwasi",
        "Francis Olweny", "Victoria Namatovu", "Henry Musoke", "Juliet Nsubuga", "Edwin Ssemwanga", "Diana Aanyu",
        "Tom Mukama", "Ruth Kawalya", "Paul Mukama", "Fiona Namuli", "Brian Kabunga", "Sarah Ainebyoona"
    )
    private val filteredPeople = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_specific_people, container, false)

        // Initialize views
        val searchBar: EditText = view.findViewById(R.id.search_bar)
        val peopleListContainer: LinearLayout = view.findViewById(R.id.people_list_container)
        val proceedButton: Button = view.findViewById(R.id.button_proceed_people_selection)

        // Hide the BottomNavigationView when this fragment is created
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav?.visibility = View.GONE

        // Initially display all people
        filteredPeople.addAll(allPeople)
        updatePeopleList(peopleListContainer)

        // Search functionality
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPeople(s.toString(), peopleListContainer)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Proceed button logic
        proceedButton.setOnClickListener {
            findNavController().navigate(R.id.action_specificPeopleFragment_to_finalVotingPageFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show the BottomNavigationView again when leaving this fragment
        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav?.visibility = View.VISIBLE
    }

    // Function to update the people list dynamically
    private fun updatePeopleList(container: LinearLayout) {
        container.removeAllViews()
        for (person in filteredPeople) {
            val checkBox = CheckBox(context)
            checkBox.text = person
            container.addView(checkBox)
        }
    }

    // Function to filter the people list
    private fun filterPeople(query: String, container: LinearLayout) {
        filteredPeople.clear()
        if (query.isEmpty()) {
            filteredPeople.addAll(allPeople)
        } else {
            for (person in allPeople) {
                if (person.contains(query, ignoreCase = true)) {
                    filteredPeople.add(person)
                }
            }
        }
        updatePeopleList(container)
    }
}
