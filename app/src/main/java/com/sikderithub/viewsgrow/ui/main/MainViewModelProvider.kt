package com.sikderithub.viewsgrow.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sikderithub.viewsgrow.repo.network.MyApi

class MainViewModelProvider(val myApi: MyApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(myApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}