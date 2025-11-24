package com.example.gihealth.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IngredientsViewModel(application: Application) : AndroidViewModel(application) {

    private val ingredientDao = IngredientsDatabase.getDatabase(application).ingredientDao()

    // StateFlow just like Journal
    private val _ingredients = MutableStateFlow<List<IngredientsEntity>>(emptyList())
    val ingredients: StateFlow<List<IngredientsEntity>> = _ingredients

    init {
        fetchAllIngredients()
    }

    fun fetchAllIngredients() {
        viewModelScope.launch {
            _ingredients.value = ingredientDao.getAllIngredients()
        }
    }

    fun addIngredient(name: String, type: String) {
        val entity = IngredientsEntity(
            name = name,
            type = type
        )

        viewModelScope.launch {
            ingredientDao.insert(entity)
            fetchAllIngredients()
        }
    }

    fun deleteIngredient(item: IngredientsEntity) {
        viewModelScope.launch {
            ingredientDao.delete(item)
            fetchAllIngredients()
        }
    }
}
