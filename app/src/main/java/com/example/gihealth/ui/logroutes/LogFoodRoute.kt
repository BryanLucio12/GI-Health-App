package com.example.gihealth.ui.logroutes


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.ui.viewmodel.FoodViewModel
import com.example.gihealth.ui.screens.LogFoodScreen
import androidx.compose.runtime.LaunchedEffect

@Composable
fun LogFoodRoute(onBackPressed: () -> Unit) {
    val vm: FoodViewModel = viewModel()
    val ctx = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) { vm.debugOpenDb() }

    LogFoodScreen(
        onSave = { food, _, _, _, _ ->
            android.widget.Toast.makeText(ctx, "Saving: $food", android.widget.Toast.LENGTH_SHORT).show()
            vm.insertFood(food)
            onBackPressed()
        },
        onBackPressed = onBackPressed
    )
}