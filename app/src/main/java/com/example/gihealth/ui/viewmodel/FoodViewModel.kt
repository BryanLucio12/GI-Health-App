package com.example.gihealth.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.gihealth.data.FoodDatabase
import com.example.gihealth.data.FoodEntity
import com.example.gihealth.data.FoodCategoryDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FoodDatabase.getDatabase(application).foodDao()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    private val _todayFoods = MutableLiveData<List<FoodEntity>>(emptyList())
    val todayFoods: LiveData<List<FoodEntity>> = _todayFoods

    // NEW: all foods for calendar/history/other screens
    private val _allFoods = MutableLiveData<List<FoodEntity>>(emptyList())
    val allFoods: LiveData<List<FoodEntity>> = _allFoods

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults: StateFlow<List<String>> = _searchResults

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories

    init {
        refreshToday()
        refreshAllFoods()
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)

    fun insertFood(name: String, time: String, meal: String, ingredients: String, date: String, categories: String) {
        viewModelScope.launch {
            dao.insert(
                FoodEntity(
                    name = name,
                    time = time,
                    meal = meal,
                    date = date,
                    ingredients = ingredients,
                    categories = categories
                )
            )
            refreshToday()
            refreshAllFoods()
        }
    }

    fun toggleCategory(category: String) {
        val current = _selectedCategories.value.toMutableList()
        if (current.contains(category)) {
            current.remove(category)
        } else {
            current.add(category)
        }
        _selectedCategories.value = current
    }

    fun setSuggestedCategories(foodName: String) {
        _selectedCategories.value = FoodCategoryDefaults.getSuggestedCategories(foodName)
    }

    fun clearSelectedCategories() {
        _selectedCategories.value = emptyList()
    }

    fun refreshToday() {
        viewModelScope.launch {
            val today = todayString()
            _todayFoods.value = dao.getFoodsForDate(today)
        }
    }

    fun refreshAllFoods() {
        viewModelScope.launch {
            _allFoods.value = dao.getAllFoods()
        }
    }

    fun debugOpenDb() {
        viewModelScope.launch {
            val foods = dao.getAllFoods()
            android.util.Log.d("FoodVM", "rows=${foods.size}")
            foods.forEach {
                android.util.Log.d("FoodVM", "food=${it.name}, date=${it.date}")
            }
        }
    }

    fun searchLoggedFoods(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResults.value =
                if (query.isBlank()) emptyList()
                else dao.searchLoggedFoodNames(query.trim())
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}