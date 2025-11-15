package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gihealth.data.FoodDatabase
import com.example.gihealth.data.FoodEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FoodDatabase.getDatabase(application).foodDao()
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    private val _todayFoods = MutableLiveData<List<FoodEntity>>(emptyList())
    val todayFoods: LiveData<List<FoodEntity>> = _todayFoods

    init {
        refreshToday()
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)

    fun insertFood(name: String, time: String, meal: String, date: String) {
        viewModelScope.launch {
            dao.insert(
                FoodEntity(
                    name = name,
                    time = time,
                    meal = meal,
                    date = date
                )
            )
            refreshToday()
        }
    }

    fun refreshToday() {
        viewModelScope.launch {
            val today = todayString()
            _todayFoods.value = dao.getFoodsForDate(today)
        }
    }

    fun debugOpenDb() {
        viewModelScope.launch {
            val cnt = dao.getAllFoods().size
            android.util.Log.d("FoodVM", "debugOpenDb rows=$cnt")
        }
    }
}
