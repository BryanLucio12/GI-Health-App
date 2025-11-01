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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.theme.GIHealthTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun LogFoodScreen(
    onSave: ((food: String, time: String, meal: String, ingredients: String, date: String) -> Unit)? = null,
    onBackPressed: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var food by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf("Lunch") }
    var ingredients by remember { mutableStateOf("") }

    //Time handling
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Meal dropdown options
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    var mealExpanded by remember { mutableStateOf(false) }

    // Ingredient
    val allIngredients = listOf("Turkey", "Lettuce", "Tomato", "Whole wheat")
    var ingredientsExpanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    val filteredIngredients = remember(ingredients, allIngredients) {
        if (ingredients.isBlank())
            emptyList()
        else allIngredients.filter { it.contains(
            ingredients,
            ignoreCase = true
        ) }
    }

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
            //Date selector
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

            // Food input
            OutlinedTextField(
                value = food,
                onValueChange = { food = it },
                label = { Text("What did you eat?") },
                placeholder = { Text("e.g., Turkey sandwich") },
                modifier = Modifier.fillMaxWidth()
            )

            // Time + Meal Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                //Time Picker
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

                //Meal Dropdown
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

            //Ingredients dropdown
            ExposedDropdownMenuBox(
                expanded = ingredientsExpanded && filteredIngredients.isNotEmpty(),
                onExpandedChange = { want -> ingredientsExpanded = want && filteredIngredients.isNotEmpty() }
            ) {
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = {
                        ingredients = it
                        ingredientsExpanded = hasFocus && it.isNotBlank() &&
                                allIngredients.any { i -> i.contains(it, ignoreCase = true) }
                    },
                    label = { Text("Ingredients") },
                    placeholder = { Text("e.g., turkey") },
                    singleLine = true,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .onFocusChanged {
                            hasFocus = it.isFocused
                            if (hasFocus) {
                                ingredientsExpanded = ingredients.isNotBlank() && filteredIngredients.isNotEmpty()
                            }
                        },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = ingredientsExpanded && filteredIngredients.isNotEmpty()
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = ingredientsExpanded && filteredIngredients.isNotEmpty(),
                    onDismissRequest = { ingredientsExpanded = false }
                ) {
                    filteredIngredients.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                ingredients = option
                                ingredientsExpanded = false
                            }
                        )
                    }
                }
            }

            //Save Button
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

@Preview(showBackground = true)
@Composable
private fun LogFoodScreenPreview() {
    GIHealthTheme {
        LogFoodScreen()
    }
}
