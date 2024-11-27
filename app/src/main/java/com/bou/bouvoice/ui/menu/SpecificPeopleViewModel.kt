package com.bou.bouvoice.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpecificPeopleViewModel : ViewModel() {

    // Mutable list for nominees
    private val _nominees = MutableLiveData<List<String>>()
    val nominees: LiveData<List<String>> get() = _nominees

    // Mutable list for voters
    private val _voters = MutableLiveData<List<String>>()
    val voters: LiveData<List<String>> get() = _voters

    // Function to set/update nominees
    fun setNominees(nomineeList: List<String>) {
        _nominees.value = nomineeList
    }

    // Function to set/update voters
    fun setVoters(voterList: List<String>) {
        _voters.value = voterList
    }
}
