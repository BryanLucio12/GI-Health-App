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
import com.example.gihealth.data.SymptomEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.gihealth.data.SymptomDatabase
import androidx.compose.foundation.lazy.items




class SymptomViewModel(application: Application) : AndroidViewModel(application) {

    //get dao from database to access the symptoms
    private val symptomDao = SymptomDatabase.getDatabase(application).symptomDao()

    private val _symptoms = MutableStateFlow<List<SymptomEntity>>(emptyList())
    val symptoms: StateFlow<List<SymptomEntity>> = _symptoms

    init {
        observeSymptoms()
    }

    //observe all symtoms in db and update stateflow
    private fun observeSymptoms() {
        viewModelScope.launch {
            symptomDao.getAllSymptoms().collectLatest { list ->
                _symptoms.value = list
            }
        }
    }

    //add new symptom
    fun addSymptom(name: String, severity: Int, timeLength: Int) {
        val entity = SymptomEntity(
            name = name,
            severity = severity,
            timeLength = timeLength,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch { symptomDao.insert(entity) }
    }

    fun deleteSymptom(symptom: SymptomEntity) {
        viewModelScope.launch { symptomDao.delete(symptom) }
    }
}

@Composable
fun SymptomScreen(
    navController: NavController,
    vm: SymptomViewModel = viewModel()
) {
    //get symptoms in viewmodel to display in UI
    val symptoms by vm.symptoms.collectAsState(initial=emptyList())

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

        items(symptoms) { symptom ->
            SymptomCard(symptom = symptom, vm = vm)
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
                if (newSymptom.isNotBlank()) {
                    vm.addSymptom(name = newSymptom, severity = 0, timeLength = 0)
                }

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
fun SymptomCard(symptom: SymptomEntity, vm: SymptomViewModel) {
    val date = java.text.SimpleDateFormat("MMM d, yyyy")
        .format(java.util.Date(symptom.timestamp))

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
            Text(symptom.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Severity: ${symptom.severity}/10", fontSize = 14.sp)
            Text("Last logged: $date", fontSize = 14.sp, color = Color.Gray)
            }

    }
}
