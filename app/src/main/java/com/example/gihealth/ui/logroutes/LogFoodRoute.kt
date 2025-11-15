package com.example.gihealth.ui.logroutes

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.ui.screens.LogFoodScreen
import com.example.gihealth.ui.viewmodel.FoodViewModel

@Composable
fun LogFoodRoute(onBackPressed: () -> Unit) {
    val vm: FoodViewModel = viewModel()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) { vm.debugOpenDb() }

    LogFoodScreen(
        onSave = { food, time, meal, ingredients, date ->
            Toast.makeText(ctx, "Saving: $food", Toast.LENGTH_SHORT).show()

            // Save ONLY food info — NOT ingredients
            vm.insertFood(
                name = food,
                time = time,
                meal = meal,
                date = date
            )

            onBackPressed()
        },
        onBackPressed = onBackPressed
    )
}
