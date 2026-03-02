package com.example.gihealth.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.gihealth.data.*

class FoodHistoryViewModel(
    private val dao: FoodDao
) : ViewModel() {

    var foods by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedFood by mutableStateOf<String?>(null)
        private set

    var lastLoggedDate by mutableStateOf<String?>(null)
        private set

    init {
        loadFoods()
    }

    private fun loadFoods() {
        viewModelScope.launch {
            foods = dao.getDistinctFoodNames()
        }
    }

    fun selectFood(food: String) {
        selectedFood = food

        viewModelScope.launch {
            val dates = dao.getDatesForFood(food)

            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH)

            lastLoggedDate = dates
                .mapNotNull {
                    try {
                        LocalDate.parse(it, formatter)
                    } catch (e: Exception) {
                        null
                    }
                }
                .maxOrNull()
                ?.format(formatter)
        }
    }
}