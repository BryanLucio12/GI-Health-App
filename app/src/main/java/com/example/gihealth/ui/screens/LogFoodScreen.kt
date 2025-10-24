@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gihealth.ui.screens

import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.theme.GIHealthTheme

@Composable
fun LogFoodScreen(
    onSave: ((food: String, time: String, meal: String, ingredients: String) -> Unit)? = null
) {
    var food by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("12:35 PM") }
    var meal by remember { mutableStateOf("Lunch") }
    var ingredients by remember { mutableStateOf("") }

    val allIngredients = remember {
        listOf("Turkey", "Lettuce", "Tomato", "Whole wheat")
    }

    var expanded by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }
    val filtered = remember(ingredients, allIngredients) {
        if (ingredients.isBlank()) emptyList()
        else allIngredients.filter { it.contains(ingredients, ignoreCase = true) }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Log a Meal") }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Replace your current date row with this:
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Left slot (fixed width), arrow aligned near Today
                Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = {}, contentPadding = PaddingValues(0.dp)) {
                        Text("<", style = MaterialTheme.typography.headlineSmall)
                    }
                }

                Spacer(Modifier.width(6.dp)) // tiny gap between arrow and text

                Text(
                    text = "Today",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.width(6.dp)) // mirror the gap for symmetry

                // Right slot (same width as left) — empty on purpose, balances the row
                Box(modifier = Modifier.width(40.dp))
            }



            OutlinedTextField(
                value = food,
                onValueChange = { food = it },
                label = { Text("What did you eat?") },
                placeholder = { Text("e.g., Turkey sandwich") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = meal,
                    onValueChange = { meal = it },
                    label = { Text("Meal") },
                    modifier = Modifier.weight(1f)
                )
            }

            // Ingredients autocomplete
            ExposedDropdownMenuBox(
                expanded = expanded && filtered.isNotEmpty(),
                onExpandedChange = { want ->
                    expanded = want && filtered.isNotEmpty()
                }
            ) {
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = {
                        ingredients = it
                        // Small logic improvement: always check for focus when text changes
                        expanded = hasFocus && it.isNotBlank() && allIngredients.any { i -> i.contains(it, ignoreCase = true) }
                    },
                    label = { Text("Ingredients") },
                    placeholder = { Text("e.g., turkey") },
                    singleLine = true,
                    modifier = Modifier
                        // THIS IS THE FIX: Use the new overload for menuAnchor
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                        .onFocusChanged {
                            hasFocus = it.isFocused
                            if (hasFocus) {
                                expanded = ingredients.isNotBlank() && filtered.isNotEmpty()
                            }
                        },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded && filtered.isNotEmpty()
                        )
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded && filtered.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    filtered.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                ingredients = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { onSave?.invoke(food, time, meal, ingredients) },
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
private fun FoodScreenPreview() {
    GIHealthTheme {
        LogFoodScreen()
    }
}
