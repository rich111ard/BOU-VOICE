package com.bou.bouvoice.ui.suggest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SuggestViewModel : ViewModel() {

    private val _suggestionText = MutableLiveData<String>()
    val suggestionText: LiveData<String> get() = _suggestionText

    // Update suggestion text in ViewModel
    fun setSuggestionText(text: String) {
        _suggestionText.value = text
    }

    // Clear suggestion text after submission
    fun clearSuggestionText() {
        _suggestionText.value = ""
    }
}
