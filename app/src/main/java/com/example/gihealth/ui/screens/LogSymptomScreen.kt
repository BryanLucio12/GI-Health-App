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
import androidx.compose.foundation.lazy.items


data class LoggedMeal(
    val food: String,
    val time: String,
    val mealType: String,
    val ingredients: String,
    val date: String
)

class FoodViewModel : ViewModel() {
    // Keeps all logged meals in memory
    var loggedMeals = mutableStateListOf<LoggedMeal>()
        private set

    fun addMeal(food: String, time: String, meal: String, ingredients: String) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        val newMeal = LoggedMeal(food, time, meal, ingredients, today)
        loggedMeals.add(newMeal)
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogSymptomScreen(
    navController: NavController,
    symptomViewModel: SymptomViewModel = viewModel()
) {
    //collect symptoms as state from viewmodel
    val databaseSymptoms by symptomViewModel.symptoms.collectAsState(initial = emptyList())

    //local state to keep track of slider rating and ntoes
    val symptomRatings = remember { mutableStateMapOf<String, Float>() }
    val symptomNotes = remember { mutableStateMapOf<String, String>() }

    val previousRoute = remember { navController.previousBackStackEntry?.destination?.route }

    //predifined symptoms from sponsor fill in pdf
    val predefinedSymptoms = listOf(
        "Abdominal pain",
        "Anorectal pain/itching",
        "Bloating/gas",
        "Blood in stool",
        "Change in bowel habits",
        "Constipation",
        "Diarrhea",
        "Incontinence of stool",
        "Heartburn/reflux",
        "Difficulty swallowing",
        "Nausea",
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

    //combine predifined and added symptoms
    val symptomsToShow = remember(databaseSymptoms) {
        (predefinedSymptoms + databaseSymptoms.map { it.name }).distinct()
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(
                    "Log Symptoms",
                    fontWeight = FontWeight.Bold
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Rate your symptoms today",
                    color = Color.DarkGray
                )
            }
            //loops through all symptoms and shows card for each
            items(symptomsToShow) { symptomName ->
                SymptomLogCard(
                    symptomName = symptomName,  // pass the name only
                    rating = symptomRatings[symptomName] ?: 0f,
                    note = symptomNotes[symptomName] ?: "",
                    onRatingChange = { newValue -> symptomRatings[symptomName] = newValue },
                    onNoteChange = { newNote -> symptomNotes[symptomName] = newNote }
                )
            }

            item {
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        symptomRatings.forEach { (name, value) ->
                            val note = symptomNotes[name] ?: ""
                            if (value > 0) {
                                symptomViewModel.addSymptom(
                                    name = name,
                                    severity = value.toInt(),
                                    timeLength = 0  // or handle as needed
                                )
                            }
                        }

                        if (previousRoute != null)
                            navController.popBackStack()
                        else navController.navigate("symptoms") {
                            popUpTo("symptoms") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        "Save Symptoms",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomLogCard(
    symptomName: String,
    rating: Float,
    note: String,
    onRatingChange: (Float) -> Unit,
    onNoteChange: (String) -> Unit
) {
    val sliderColor = Color(0xFF0F9D58)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(symptomName, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            // Show current value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Severity", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "${rating.toInt()} / 10",
                    color = sliderColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Slider(
                value = rating,
                onValueChange = onRatingChange,
                valueRange = 0f..10f,
                steps = 9,
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
