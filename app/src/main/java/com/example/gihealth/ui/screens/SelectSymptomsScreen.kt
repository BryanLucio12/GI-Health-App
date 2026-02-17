package com.example.gihealth.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gihealth.ui.viewmodel.LogSymptomsViewModel

@Composable
fun SelectSymptomsScreen(
    navController: NavController
) {
    val activity = LocalContext.current as ComponentActivity
    val logVm: LogSymptomsViewModel = viewModel(activity)

    var search by remember { mutableStateOf("") }

    // Predefined symptoms
    val predefinedSymptoms = listOf(
        "Abdominal pain",
        "Anorectal pain/itching",
        "Bloating/gas",
        "Blood in stool",
        "Bowel Movement",
        "Constipation",
        "Diarrhea",
        "Incontinence of stool",
        "Heartburn/reflux",
        "Difficulty swallowing",
        "Nausea",
        "Right upper abdominal pain",
        "Kidney pain",
        "Vomiting",
        "Black tarry stools",
        "Dark urine",
        "Heavy menstruation",
        "Pregnancy",
        "Frequent urination",
        "Blood in urine",
        "Itching",
        "Jaundice",
        "Rashes",
        "Frequent headaches",
        "Memory loss/confusion",
        "Numbness or tingling",
        "Cold intolerance",
        "Excessive thirst",
        "Fatigue",
        "Fever",
        "Loss of appetite",
        "Night sweats",
        "Weight gain",
        "Weight loss",
        "Anxiety",
        "Depression",
        "Double vision",
        "Eye irritation",
        "Eye pain",
        "Eye Redness",
        "Sore throat",
        "Hoarseness",
        "Mouth sores",
        "Easy bruising",
        "Prolonged bleeding",
        "Back pain",
        "Joint pain",
        "Frequent cough",
        "Snoring",
        "Sleep apnea",
        "Wheezing",
        "Shortness of breath",
        "Allergies"
    )


    val allSymptoms = remember(logVm.customSymptoms) {
        (predefinedSymptoms + logVm.customSymptoms).distinct()
    }

    val filtered = allSymptoms.filter {
        it.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Select Symptoms",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search symptoms") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { symptom ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { logVm.toggleSymptom(symptom) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = logVm.selected.any { it.name == symptom },
                        onCheckedChange = { logVm.toggleSymptom(symptom) },
                        modifier = Modifier.scale(0.8f),
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF0F9D58),
                            uncheckedColor = Color.Gray,
                            checkmarkColor = Color.White
                        )
                    )

                    Spacer(Modifier.width(8.dp))
                    Text(symptom)
                }
            }
        }

        Button(
            onClick = { navController.navigate("rate_symptoms") },
            enabled = logVm.selected.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F9D58),
                contentColor = Color.White
            )
        ) {
            Text("Continue")
        }
    }
}