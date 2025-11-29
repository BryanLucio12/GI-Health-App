@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gihealth.ui.screens

import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.viewmodel.FoodViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun LogFoodScreen(
    foodViewModel: FoodViewModel,
    onSave: ((food: String, time: String, meal: String, ingredients: String, date: String) -> Unit)? = null,
    onBackPressed: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var food by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf("Lunch") }

    // Ingredients as editable list and text field for adding new items
    var ingredientsList by remember { mutableStateOf<List<String>>(emptyList()) }
    var newIngredientText by remember { mutableStateOf("") }

    // For showing long ingredient text in a dialog
    var selectedIngredientForDialog by remember { mutableStateOf<String?>(null) }

    // Observe search results from catalog
    val searchResults by foodViewModel.searchResults.collectAsState()

    // Time handling
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Meal dropdown options
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    var mealExpanded by remember { mutableStateOf(false) }

    // Date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    // Scroll state for whole screen
    val scrollState = rememberScrollState()

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
                .fillMaxSize()
                .verticalScroll(scrollState),
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

            // Food Dropdown
            OutlinedTextField(
                value = food,
                onValueChange = {
                    food = it
                    // Clear any previous auto ingredients if user edits the food name
                    ingredientsList = emptyList()
                    newIngredientText = ""
                    foodViewModel.searchFoods(it)
                },
                label = { Text("What did you eat?") },
                placeholder = { Text("e.g., Turkey sandwich") },
                modifier = Modifier.fillMaxWidth()
            )

            // Suggestions list below the text field
            if (food.isNotBlank() && searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        searchResults.forEach { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Auto-fill both the food name and ingredients list from catalog
                                        food = item.name

                                        val parsed = item.ingredients
                                            .split(',', ';', '\n')
                                            .map { it.trim() }
                                            .filter { it.isNotEmpty() }

                                        ingredientsList = parsed
                                        newIngredientText = ""
                                        // keep suggestions until user types again, or you can clear search:
                                        foodViewModel.searchFoods("") // optional: clear suggestions
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
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

            // Editable ingredients section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                // Existing ingredients list with remove buttons
                if (ingredientsList.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ingredientsList.forEachIndexed { index, ingredient ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Truncated preview, tap to see full in dialog
                                Text(
                                    text = ingredient,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedIngredientForDialog = ingredient
                                        }
                                )
                                IconButton(
                                    onClick = {
                                        ingredientsList =
                                            ingredientsList.toMutableList().also { it.removeAt(index) }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove ingredient"
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No ingredients yet. Add some below.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Add new ingredient row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newIngredientText,
                        onValueChange = { newIngredientText = it },
                        label = { Text("Add ingredient") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            val cleaned = newIngredientText.trim()
                            if (cleaned.isNotEmpty()) {
                                ingredientsList = ingredientsList + cleaned
                                newIngredientText = ""
                            }
                        },
                        enabled = newIngredientText.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    val ingredientsString = ingredientsList.joinToString(", ")

                    onSave?.invoke(
                        food,
                        selectedTime.format(timeFormatter),
                        meal,
                        ingredientsString,
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

    // Dialog showing full ingredient text when tapped
    if (selectedIngredientForDialog != null) {
        AlertDialog(
            onDismissRequest = { selectedIngredientForDialog = null },
            confirmButton = {
                TextButton(onClick = { selectedIngredientForDialog = null }) {
                    Text("Close")
                }
            },
            title = { Text("Ingredient") },
            text = {
                Text(
                    text = selectedIngredientForDialog ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        )
    }
}
