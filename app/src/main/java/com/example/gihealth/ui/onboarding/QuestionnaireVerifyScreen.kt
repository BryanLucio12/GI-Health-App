package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application
import com.example.gihealth.data.SecurityQuestionEntity
import com.example.gihealth.data.SecurityQuestionViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun QuestionnaireVerifyScreen(
    navController: NavController,
    onVerified: () -> Unit
) {
    val context = LocalContext.current

    val securityVm: SecurityQuestionViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    var savedQuestions by remember { mutableStateOf<List<SecurityQuestionEntity>>(emptyList()) }
    var userAnswers by remember { mutableStateOf(MutableList(3) { "" }) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val questions = securityVm.getSecurityQuestions()
        savedQuestions = questions.take(3)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "Verify Your Identity",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Answer your security questions to reset your PIN.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (savedQuestions.isEmpty()) {
            CircularProgressIndicator()
            return@Column
        }

        savedQuestions.forEachIndexed { index, item ->
            Text(
                text = item.question,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = userAnswers[index],
                onValueChange = { newText ->
                    userAnswers = userAnswers.toMutableList().also { list ->
                        list[index] = newText
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter your answer") }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (errorMessage.isNotBlank()) {
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val correct = savedQuestions.zip(userAnswers).all { (entity, typedAnswer) ->
                    entity.answer.equals(typedAnswer.trim(), ignoreCase = true)
                }

                if (correct) {
                    onVerified()
                } else {
                    errorMessage = "One or more answers are incorrect."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
        ) {
            Text(
                "Continue",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
