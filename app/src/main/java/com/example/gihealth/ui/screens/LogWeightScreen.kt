package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.MenuAnchorType
import com.example.gihealth.data.WellBeingEntity
import com.example.gihealth.data.WellBeingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWeightScreen(navController: NavController) {
    val viewModel: WellBeingViewModel = viewModel()
    // state
    var weightText by remember { mutableStateOf("") }
    var weightError by remember { mutableStateOf<String?>(null) }

    val units = listOf("lb", "kg", "st")
    var selectedUnit by remember { mutableStateOf(units.first()) }
    var unitMenuExpanded by remember { mutableStateOf(false) }

    var sleepRating by remember { mutableFloatStateOf(5f) }
    var stressRating by remember { mutableFloatStateOf(5f) }

    var sleepNote by remember { mutableStateOf("") }
    var stressNote by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Daily Wellbeing", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {



            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        selectedDate = selectedDate.minusDays(1)   // ◀ previous day
                    }) {
                        Text("<", fontSize = 22.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = if (selectedDate == LocalDate.now())
                            "Today (${selectedDate.format(dateFormatter)})"
                        else selectedDate.format(dateFormatter),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )

                    Spacer(Modifier.width(6.dp))

                    TextButton(onClick = {
                        selectedDate = selectedDate.plusDays(1)    // ▶ next day
                    }) {
                        Text(">", fontSize = 22.sp, color = Color.Gray)
                    }
                }
            }

            // weight input
            item {
                Text("Enter your weight", fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { newValue ->
                        // allow only digits
                        if (newValue.all { it.isDigit() }) {
                            weightText = newValue
                            val numeric = newValue.toIntOrNull()

                            weightError = when {
                                newValue.isEmpty() -> "Weight is required"
                                numeric == null -> "Invalid number"
                                numeric !in 0..1000 -> "Must be between 0 and 1000"
                                else -> null
                            }
                        }
                    },
                    isError = weightError != null,
                    label = { Text("Weight") },
                    placeholder = { Text("0 - 1000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                weightError?.let {
                    Text(it, color = Color.Red, fontSize = 13.sp)
                }

                // unit dropdown
                ExposedDropdownMenuBox(
                    expanded = unitMenuExpanded,
                    onExpandedChange = { unitMenuExpanded = !unitMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedUnit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(unitMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = unitMenuExpanded,
                        onDismissRequest = { unitMenuExpanded = false }
                    ) {
                        units.forEach { unit ->
                            DropdownMenuItem(
                                text = { Text(unit) },
                                onClick = {
                                    selectedUnit = unit
                                    unitMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // sleep tracker
            item {
                WellbeingSliderCard(
                    title = "How was your sleep last night?",
                    value = sleepRating,
                    note = sleepNote,
                    onValueChange = { sleepRating = it },
                    onNoteChange = { sleepNote = it }
                )
            }

            // stress tracker
            item {
                WellbeingSliderCard(
                    title = "Stress level",
                    value = stressRating,
                    note = stressNote,
                    onValueChange = { stressRating = it },
                    onNoteChange = { stressNote = it }
                )
            }

            // Save
            item {
                Button(
                    onClick = {
                        if (weightError == null && weightText.isNotEmpty()) {
                            val formattedDate =
                                selectedDate.format(dateFormatter)
                            val entry = WellBeingEntity(
                                timestamp = System.currentTimeMillis(),
                                weight = weightText.toFloat(),
                                unit = selectedUnit,
                                sleepRating = sleepRating.toInt(),
                                sleepNote = sleepNote,
                                stressRating = stressRating.toInt(),
                                stressNote = stressNote,
                                date= formattedDate
                            )

                            viewModel.insertEntry(entry)
                            navController.popBackStack()
                        }
                    },
                    enabled = weightError == null && weightText.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F9D58)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Save", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}



// reusable card for wellbeing
@Composable
fun WellbeingSliderCard(
    title: String,
    value: Float,
    note: String,
    onValueChange: (Float) -> Unit,
    onNoteChange: (String) -> Unit
) {
    val sliderColor = Color(0xFF0F9D58)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Level", color = Color.Gray)
                Text("${value.toInt()} / 10",
                    color = sliderColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = value,
                onValueChange = onValueChange,
                steps = 8,
                valueRange = 1f..10f,
                colors = SliderDefaults.colors(
                    thumbColor = sliderColor,
                    activeTrackColor = sliderColor,
                    inactiveTrackColor = sliderColor.copy(alpha = 0.3f)
                )

            )
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text("Add a note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}
