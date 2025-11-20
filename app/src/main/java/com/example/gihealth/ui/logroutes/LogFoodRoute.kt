package com.example.gihealth.ui.logroutes

import androidx.compose.runtime.Composable
import com.example.gihealth.ui.screens.LogFoodScreen
import com.example.gihealth.ui.viewmodel.FoodViewModel

@Composable
fun LogFoodRoute(
    foodViewModel: FoodViewModel,
    onSave: (food: String, time: String, meal: String, ingredients: String, date: String) -> Unit,
    onBackPressed: () -> Unit
) {
    LogFoodScreen(
        foodViewModel = foodViewModel,
        onSave = onSave,
        onBackPressed = onBackPressed
    )
}
