package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import androidx.compose.ui.platform.LocalContext
import com.example.gihealth.data.SecurityQuestionEntity
import com.example.gihealth.data.SecurityQuestionViewModel

@Composable
fun QuestionnaireScreen(
    navController: NavController,
    onComplete: () -> Unit
) {
    val context = LocalContext.current

    val securityVm: SecurityQuestionViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    // Question asked
    val allQuestions = listOf(
        "What is your favorite food?",
        "What city were you born in?",
        "What is your favorite color?",
        "What was the name of your first pet?",
        "What is your dream vacation spot?",
        "What is your favorite movie?",
        "What high school did you attend?"
    )

    var selectedQuestions by remember { mutableStateOf(listOf<String>()) }
    var answers by remember { mutableStateOf(mapOf<String, String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = "Security Questions",
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Choose 3 questions and provide your answers.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Remaining questions to choose from
        val remainingQuestions = allQuestions.filterNot { selectedQuestions.contains(it) }

        if (selectedQuestions.size < 3) {
            Text("Choose Question ${selectedQuestions.size + 1}", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            remainingQuestions.forEach { question ->
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                    onClick = {
                        selectedQuestions = selectedQuestions + question
                        answers = answers + (question to "")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                ) {
                    Text(question, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Answers saved
        selectedQuestions.forEach { question ->
            Text(question, fontWeight = FontWeight.SemiBold)

            OutlinedTextField(
                value = answers[question] ?: "",
                onValueChange = { newValue ->
                    answers = answers.toMutableMap().apply {
                        this[question] = newValue
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your answer") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Only continue when all 3 are answered
        val allAnswered = selectedQuestions.size == 3 && answers.values.all { it.isNotBlank() }

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
            onClick = {
                // Build DB list to store
                val itemsToSave = selectedQuestions.map { question ->
                    SecurityQuestionEntity(
                        question = question,
                        answer = answers[question] ?: ""
                    )
                }

                // Save to database
                securityVm.saveSecurityQuestions(itemsToSave)

                // Go on to user set up
                navController.navigate("user_setup") {
                    popUpTo("questionnaire") { inclusive = true }
                }

                onComplete()
            },
            enabled = allAnswered,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Continue", fontSize = 18.sp)
        }
    }
}