package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gihealth.data.FoodDatabase
import com.example.gihealth.data.FoodEntity
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = FoodDatabase.getDatabase(application).foodDao()

    init {
        // also force-open on init just to be safe
        viewModelScope.launch {
            try {
                val cnt = dao.getAllFoods().size
                android.util.Log.d("FoodVM", "init opened DB, rows=$cnt")
            } catch (t: Throwable) {
                android.util.Log.e("FoodVM", "init open failed", t)
            }
        }
    }

    fun insertFood(name: String) {
        viewModelScope.launch {
            dao.insert(FoodEntity(name = name))
            android.util.Log.d("FoodVM", "Inserted: $name")
        }
    }

    fun debugOpenDb() {
        viewModelScope.launch {
            val cnt = dao.getAllFoods().size
            android.util.Log.d("FoodVM", "debugOpenDb rows=$cnt")
        }
    }
}