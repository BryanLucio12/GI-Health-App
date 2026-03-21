@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.gihealth.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.viewmodel.FoodViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.style.TextAlign

@Composable
fun LogFoodScreen(
    foodViewModel: FoodViewModel,
    onSave: ((food: String, time: String, meal: String, ingredients: String, date: String) -> Unit)? = null,
    onBackPressed: (() -> Unit)? = null
) {
    var food by remember { mutableStateOf("") }
    var meal by remember { mutableStateOf("Lunch") }

    val searchResults by foodViewModel.searchResults.collectAsState()

    // Time
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Meal dropdown
    val mealOptions = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    var mealExpanded by remember { mutableStateOf(false) }

    // Date
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    val scrollState = rememberScrollState()

    BackHandler { onBackPressed?.invoke() }

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Text("<", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text = if (selectedDate == LocalDate.now())
                        "Today (${selectedDate.format(dateFormatter)})"
                    else selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.width(6.dp))

                TextButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Text(">", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                }
            }

            OutlinedTextField(
                value = food,
                onValueChange = {
                    food = it
                    foodViewModel.searchLoggedFoods(it)
                },
                label = { Text("What did you eat?") },
                placeholder = { Text("e.g., Turkey sandwich") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (food.isNotBlank() && searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column {
                        searchResults.forEach { item ->
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        food = item
                                        foodViewModel.clearSearchResults()
                                    }
                                    .padding(12.dp)
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showTimePicker = true }
                ) {
                    OutlinedTextField(
                        value = selectedTime.format(timeFormatter),
                        onValueChange = {},
                        label = { Text("Time") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = MaterialTheme.colorScheme.onSurface
                        ),
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = mealExpanded,
                    onExpandedChange = { mealExpanded = !mealExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = meal,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true
                            )
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = mealExpanded,
                        onDismissRequest = { mealExpanded = false }
                    ) {
                        mealOptions.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    meal = it
                                    mealExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    onSave?.invoke(
                        food.trim(),
                        selectedTime.format(timeFormatter),
                        meal,
                        "",
                        selectedDate.format(dateFormatter)
                    )
                    foodViewModel.clearSearchResults()
                },
                enabled = food.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58),
                    contentColor = Color.White
                )
            ) {
                Text("Save Log")
            }
        }
    }

    if (showTimePicker) {
        TimePickerDropdownDialog(
            initialTime = selectedTime,
            onDismiss = { showTimePicker = false },
            onConfirm = {
                selectedTime = it
                showTimePicker = false
            }
        )
    }
}

@Composable
fun TimePickerDropdownDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    var hour by remember { mutableIntStateOf(if (initialTime.hour % 12 == 0) 12 else initialTime.hour % 12) }
    var minute by remember { mutableIntStateOf(initialTime.minute) }
    var isAm by remember { mutableStateOf(initialTime.hour < 12) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time", fontWeight = FontWeight.Bold) },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeColumn(
                    items = (1..12).toList(),
                    selected = hour,
                    onSelect = { hour = it },
                    startAtFirstItem = true
                )

                TimeColumn(
                    items = (0..59).toList(),
                    selected = minute,
                    formatter = { "%02d".format(it) },
                    onSelect = { minute = it },
                    startAtFirstItem = true
                )

                Column(
                    modifier = Modifier
                        .height(140.dp)
                        .width(64.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    listOf("AM", "PM").forEach { period ->
                        Text(
                            text = period,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAm = period == "AM" }
                                .padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = if ((isAm && period == "AM") || (!isAm && period == "PM")) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h24 = when {
                        isAm && hour == 12 -> 0
                        !isAm && hour == 12 -> 12
                        !isAm -> hour + 12
                        else -> hour
                    }
                    onConfirm(LocalTime.of(h24, minute))
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun <T> TimeColumn(
    items: List<T>,
    selected: T,
    formatter: (T) -> String = { it.toString() },
    onSelect: (T) -> Unit,
    startAtFirstItem: Boolean = false
) {
    val repeatCount = 1000
    val listSize = items.size
    val initialIndex = if (startAtFirstItem) {
        repeatCount / 2 * listSize
    } else {
        repeatCount / 2 * listSize + items.indexOf(selected)
    }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(140.dp)
            .width(64.dp)
    ) {
        items(repeatCount * listSize) { index ->
            val item = items[index % listSize]
            Text(
                text = formatter(item),
                textAlign = TextAlign.Center,
                fontWeight = if (item == selected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(item) }
                    .padding(vertical = 8.dp)
            )
        }
    }
}