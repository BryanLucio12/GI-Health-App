package com.example.gihealth.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gihealth.ui.viewmodel.LogSymptomsViewModel
import com.example.gihealth.data.SymptomViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.*
import java.time.ZoneId





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateSymptomsScreen(
    navController: NavController,
    symptomVm: SymptomViewModel = viewModel()
) {

    val activity = LocalContext.current as ComponentActivity
    val logVm: LogSymptomsViewModel = viewModel(activity)

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Rate Symptoms",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))

                    logVm.selected.forEach { symptom ->
                        if (symptom.severity > 0) {


                            symptomVm.addSymptom(
                                name = symptom.name,
                                severity = symptom.severity,
                                timeLength = 0,
                                date=formattedDate)
                        }
                    }
                    logVm.clear()
                    navController.navigate("symptoms") {
                        popUpTo("select_symptoms") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "Save Symptoms",
                    fontSize = 18.sp
                )
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            // 🔹 NEW: Date selector (matches LogFoodScreen UI)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Text("<", style = MaterialTheme.typography.headlineSmall, color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(6.dp))

                Text(
                    text = if (selectedDate == LocalDate.now())
                        "Today (${selectedDate.format(dateFormatter)})"
                    else selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.width(6.dp))

                TextButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Text(
                        ">",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold)
                }
            }


            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(logVm.selected) { symptom ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = symptom.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Severity",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = "${symptom.severity} / 10",
                                    color = Color(0xFF0F9D58),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                            Slider(
                                value = symptom.severity.toFloat(),
                                onValueChange = {
                                    logVm.updateSeverity(symptom.name, it.toInt())
                                },
                                valueRange = 0f..10f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF0F9D58),
                                    activeTrackColor = Color(0xFF0F9D58),
                                    inactiveTrackColor = Color(0xFF0F9D58).copy(alpha = 0.3f)
                                )
                            )

                            OutlinedTextField(
                                value = symptom.note,
                                onValueChange = {
                                    logVm.updateNote(symptom.name, it)
                                },
                                placeholder = { Text("Add a note (optional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}