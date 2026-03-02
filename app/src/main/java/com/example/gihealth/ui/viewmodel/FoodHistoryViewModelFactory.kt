package com.example.gihealth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gihealth.data.FoodDao

class FoodHistoryViewModelFactory(
    private val dao: FoodDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodHistoryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}