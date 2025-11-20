@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gihealth.ui.screens

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.viewmodel.FoodViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color

@Composable
fun LogFoodScreen(
    foodViewModel: FoodViewModel,
    onSave: ((food: String, time: String, meal: String, ingredients: String, date: String) -> Unit)? = null,
    onBackPressed: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var food by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf("Lunch") }
    var ingredients by remember { mutableStateOf("") }   // auto-filled only

    // Observe search results from catalog
    val searchResults by foodViewModel.searchResults.collectAsState()
    var foodDropdownExpanded by remember { mutableStateOf(false) }

    // Time handling
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Meal dropdown options
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    var mealExpanded by remember { mutableStateOf(false) }

    // Date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    // Handle system back gesture
    BackHandler {
        onBackPressed?.invoke()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Log a Meal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed?.invoke() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Text("<", style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text = if (selectedDate == LocalDate.now())
                        "Today (${selectedDate.format(dateFormatter)})"
                    else selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(6.dp))

                TextButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Text(">", style = MaterialTheme.typography.headlineSmall)
                }
            }

            // Food input with dropdown suggestions (from branded catalog)
            ExposedDropdownMenuBox(
                expanded = foodDropdownExpanded && searchResults.isNotEmpty(),
                onExpandedChange = { expanded ->
                    foodDropdownExpanded = expanded && searchResults.isNotEmpty()
                }
            ) {
                OutlinedTextField(
                    value = food,
                    onValueChange = {
                        food = it
                        ingredients = "" // clear any previous auto ingredients if user edits
                        foodViewModel.searchFoods(it)
                        foodDropdownExpanded = it.isNotBlank()
                    },
                    label = { Text("What did you eat?") },
                    placeholder = { Text("e.g., Turkey sandwich") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = foodDropdownExpanded && searchResults.isNotEmpty(),
                    onDismissRequest = { foodDropdownExpanded = false }
                ) {
                    searchResults.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.name) },
                            onClick = {
                                // Auto-fill both the food name and ingredients from catalog
                                food = item.name
                                ingredients = item.ingredients
                                foodDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Time + Meal Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Time Picker
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Time") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable {
                                TimePickerDialog(
                                    context,
                                    { _, hour: Int, minute: Int ->
                                        selectedTime = LocalTime.of(hour, minute)
                                    },
                                    selectedTime.hour,
                                    selectedTime.minute,
                                    false
                                ).show()
                            }
                    )
                }

                // Meal Dropdown
                ExposedDropdownMenuBox(
                    expanded = mealExpanded,
                    onExpandedChange = { mealExpanded = !mealExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = meal,
                        onValueChange = {},
                        label = { Text("Meal") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = mealExpanded,
                        onDismissRequest = { mealExpanded = false }
                    ) {
                        mealOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    meal = option
                                    mealExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Read-only view of auto-filled ingredients (if available)
            if (ingredients.isNotBlank()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Ingredients (auto-filled):",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = ingredients,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    onSave?.invoke(
                        food,
                        selectedTime.format(timeFormatter),
                        meal,
                        ingredients,
                        selectedDate.format(dateFormatter)
                    )
                },
                enabled = food.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Log")
            }
        }
    }
}
