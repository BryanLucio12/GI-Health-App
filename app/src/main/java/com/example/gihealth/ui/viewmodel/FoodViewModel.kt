package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gihealth.data.FoodCatalogEntity
import com.example.gihealth.data.FoodDatabase
import com.example.gihealth.data.FoodEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    // DAO for logged foods (what user ate) - same as before
    private val dao = FoodDatabase.getDatabase(application).foodDao()

    // NEW: DAO for USDA/SR Legacy catalog (for suggestions)
    private val catalogDao = FoodDatabase.getDatabase(application).foodCatalogDao()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    // Existing: today's logged foods
    private val _todayFoods = MutableLiveData<List<FoodEntity>>(emptyList())
    val todayFoods: LiveData<List<FoodEntity>> = _todayFoods

    // NEW: search results from the catalog
    private val _searchResults = MutableStateFlow<List<FoodCatalogEntity>>(emptyList())
    val searchResults: StateFlow<List<FoodCatalogEntity>> = _searchResults

    init {
        refreshToday()
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)


    fun insertFood(name: String, time: String, meal: String, ingredients: String, date: String) {
        viewModelScope.launch {
            dao.insert(
                FoodEntity(
                    name = name,
                    time = time,
                    meal = meal,
                    date = date,
                    ingredients = ingredients
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

    // === NEW: search function for SR Legacy catalog ===
    fun searchFoods(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResults.value =
                if (query.isBlank()) emptyList()
                else catalogDao.searchFoods(query)
        }
    }
}
