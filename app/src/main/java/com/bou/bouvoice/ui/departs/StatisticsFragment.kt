package com.bou.bouvoice.ui.departs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Manually reference the layout from the layout_dpt folder
        val layoutId = resources.getIdentifier("fragment_statistics", "layout", requireContext().packageName)
        return inflater.inflate(layoutId, container, false)
    }
}
