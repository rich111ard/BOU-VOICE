package com.bou.bouvoice.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NomineeViewModel : ViewModel() {

    private val _nominees = MutableLiveData<List<Nominee>>(emptyList())
    val nominees: LiveData<List<Nominee>> get() = _nominees

    // Add a nominee to the list
    fun addNominee(nominee: Nominee) {
        _nominees.value = _nominees.value?.plus(nominee)
    }

    // Remove a nominee from the list
    fun removeNominee(index: Int) {
        _nominees.value = _nominees.value?.toMutableList()?.apply { removeAt(index) }
    }
}
