package com.example.gihealth.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class SymptomViewModel : ViewModel() {

    private val defaultSymptoms = listOf("Bloating", "Cramps", "Fatigue", "Nausea", "Headache")

    // List of symptom names
    var symptoms = mutableStateListOf<String>()
        private set

    // Logged symptom data
    var loggedSymptoms = mutableStateMapOf<String, SymptomLogEntry>()
        private set

    init {
        symptoms.addAll(defaultSymptoms)
    }

    fun addSymptom(name: String) {
        if (name.isNotBlank() && !symptoms.contains(name.trim())) {
            symptoms.add(name.trim())
        }
    }

    fun logSymptom(name: String, severity: Int, note: String) {
        val today = LocalDate.now()
        val dateString = today.format(DateTimeFormatter.ofPattern("MMM d"))
        loggedSymptoms[name] = SymptomLogEntry(severity, note, dateString)
    }

    data class SymptomLogEntry(
        val severity: Int,
        val note: String,
        val date: String
    )
}
@Composable
fun SymptomScreen(
    navController: NavController,
    vm: SymptomViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                "Symptoms",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Track and monitor your symptoms",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("logSymptom") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(45.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("Log Symptom", color = Color.White, fontSize = 16.sp)
            }
        }

        items(vm.symptoms.size) { index ->
            SymptomCard(symptomName = vm.symptoms[index], vm = vm)
        }

        item {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.2.dp, Color.Gray),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF0F9D58))
                Spacer(Modifier.width(6.dp))
                Text("Add Symptom", color = Color(0xFF0F9D58), fontWeight = FontWeight.Medium)
            }
        }
    }

    if (showAddDialog) {
        AddSymptomDialog(
            onDismiss = { showAddDialog = false },
            onAddSymptom = { newSymptom ->
                vm.addSymptom(newSymptom)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddSymptomDialog(
    onDismiss: () -> Unit,
    onAddSymptom: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a New Symptom", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Enter the name of the symptom you want to track:")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = { Text("e.g., Heartburn, Gas, Acid Reflux") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddSymptom(text) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SymptomCard(symptomName: String, vm: SymptomViewModel) {
    val logEntry = vm.loggedSymptoms[symptomName]

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(symptomName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            if (logEntry != null) {
                Text("Severity: ${logEntry.severity}/10", fontSize = 14.sp)
                if (logEntry.note.isNotBlank()) Text("Note: ${logEntry.note}", fontSize = 14.sp)
                Text("Last logged: ${logEntry.date}", fontSize = 14.sp, color = Color.Gray)
            } else {
                Text("No data yet.", color = Color.Gray)
            }
        }
    }
}
