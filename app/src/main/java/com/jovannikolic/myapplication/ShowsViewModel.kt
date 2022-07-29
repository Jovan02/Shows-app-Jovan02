package com.jovannikolic.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import models.Show

class ShowsViewModel : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    fun setShowsList(shows: List<Show>) {
        _showsLiveData.value = shows
    }

    init {
        _showsLiveData.value = emptyList()
    }
}