package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gihealth.data.SymptomEntity
import com.example.gihealth.models.PdfQuestionnaireAnswers
import com.example.gihealth.utils.generatePdfReport
import kotlin.Int

@Composable
fun UserPDFQuestionnaire(
    navController: NavController,
    symptoms: List<SymptomEntity>,
) {
    var answers by remember {
        mutableStateOf(
            PdfQuestionnaireAnswers(
                // Question 1 – Challenges
                eatLess = null,
                declineSocial = null,
                avoidActivities = null,
                arriveLateLeaveEarly = null,
                missWorkOrSchool = null,
                loseSexualDesire = null,
                inBedAllOrMostOfDay = null,

                // Question 2 – Emotions
                anxious = null,
                depressed = null,
                frustrated = null,
                isolated = null,
                stressed = null,
                helpless = null,
                overwhelmed = null,
                angry = null,
                sad = null,
                embarrassed = null,
                guilty = null,
                noneOfTheAbove = null,

                // Question 3 – Appetite
                appetite = null,

                // Question 9 – Improvements
                question9a = null,
                question9b = null,
                question9c = null,

                question10a = null
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            text = "Answer a few questions to complete your report",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))


        Text("Question 1 – Challenges", style = MaterialTheme.typography.titleMedium)

        FrequencyQuestion("Eat less", answers.eatLess){
            answers = answers.copy(eatLess = it)
        }

        FrequencyQuestion("Decline social engagements", answers.declineSocial){
            answers = answers.copy(declineSocial = it)
        }

        FrequencyQuestion("Avoid activities I enjoy", answers.avoidActivities){
            answers = answers.copy(avoidActivities = it)
        }

        FrequencyQuestion("Arrive late or leave early", answers.arriveLateLeaveEarly){
            answers = answers.copy(arriveLateLeaveEarly = it)
        }

        FrequencyQuestion("Miss work or school", answers.missWorkOrSchool){
            answers = answers.copy(missWorkOrSchool = it)
        }


        FrequencyQuestion("Lose Sexual Desire", answers.loseSexualDesire){
            answers = answers.copy(loseSexualDesire = it)
        }

        FrequencyQuestion("Stay in bed for all or most of the day", answers.inBedAllOrMostOfDay){
            answers = answers.copy(inBedAllOrMostOfDay = it)
        }

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            text = "Question 2 – Emotions (Select all that apply)",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(modifier = Modifier.weight(1f)){

                EmotionCheckbox("Anxious", answers.anxious){
                    answers = answers.copy(anxious = it)
                }

                EmotionCheckbox("Depressed", answers.depressed){
                    answers = answers.copy(depressed = it)
                }
                EmotionCheckbox("Frustrated", answers.frustrated){
                    answers = answers.copy(frustrated = it)
                }
                EmotionCheckbox("Isolated", answers.isolated){
                    answers = answers.copy(isolated = it)
                }

                EmotionCheckbox("Stressed", answers.stressed){
                    answers = answers.copy(stressed = it)
                }
                EmotionCheckbox("Helpless", answers.helpless){
                    answers = answers.copy(helpless = it)
                }
            }

            Column(modifier = Modifier.weight(1f)){

                EmotionCheckbox("Overwhelmed", answers.overwhelmed){
                    answers = answers.copy(overwhelmed = it)
                }

                EmotionCheckbox("Angry", answers.angry){
                    answers = answers.copy(angry = it)
                }

                EmotionCheckbox("Sad", answers.sad){
                    answers = answers.copy(sad = it)
                }

                EmotionCheckbox("Embarrassed", answers.embarrassed){
                    answers = answers.copy(embarrassed = it)
                }

                EmotionCheckbox("Guilty", answers.guilty){
                    answers = answers.copy(guilty = it)
                }

                EmotionCheckbox("None of the above", answers.noneOfTheAbove){
                    answers = answers.copy(noneOfTheAbove = it)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Question 3 – Appetite", style = MaterialTheme.typography.titleMedium)

        Text(
            "Over the past month, my appetite has:",
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp))
        {
            listOf(
                "Increased" to 0,
                "Decreased" to 1,
                "Stayed the same" to 2
            ).forEach { (label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically)
                {
                    RadioButton(
                        selected = answers.appetite == value,
                        onClick = { answers = answers.copy(appetite = value) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0F9D58)
                        )
                    )
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))


        Text("Question 4 – Improvements", style = MaterialTheme.typography.titleMedium)

        Text(
            "Since starting my current treatment, my disease symptoms have:",
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        Column{
            listOf(
                "Improved" to 0,
                "Become worse" to 1,
                "Stayed the same" to 2
            ).forEach { (label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically){
                    RadioButton(
                        selected = answers.question9a == value,
                        onClick = { answers = answers.copy(question9a = value) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0F9D58)
                        )
                    )
                    Text(label)
                }
            }
        }

        if (answers.question9a == 0)
        {
            OutlinedTextField(
                value = answers.question9b ?: "",
                onValueChange = { answers = answers.copy(question9b = it) },
                label = { Text("Please explain how they have improved") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        if (answers.question9a == 1)
        {
            OutlinedTextField(
                value = answers.question9c ?: "",
                onValueChange = { answers = answers.copy(question9c = it) },
                label = { Text("Please explain how they have worsened") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Question 5 – Primary Concern",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "My primary concern for today’s office visit is:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = answers.question10a ?: "",
            onValueChange = {
                answers = answers.copy(question10a = it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            placeholder = {
                Text("Type your primary concern here…")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0F9D58),
                cursorColor = Color(0xFF0F9D58)
            ),
            maxLines = 6
        )
        val context = LocalContext.current

        Button(
            onClick = {
                generatePdfReport(
                    context = context,
                    symptoms = symptoms,
                    answers = answers
                )

                navController.navigate("add")
                {
                    popUpTo("user_pdf_questionnaire")
                    {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F9D58),
                contentColor = Color.White
            )
        ) {
            Text("Generate PDF")
        }
    }
}


@Composable
fun FrequencyQuestion(label: String, value: Int?, onValueChange: (Int) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label)
        Row {
            listOf("Never" to 0, "Sometimes" to 1, "Often" to 2).forEach { (text, v) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = value == v,
                        onClick = { onValueChange(v) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0F9D58)
                        )
                    )
                    Text(text)
                }
            }
        }
    }
}

@Composable
fun EmotionCheckbox(label: String, value: Int?, onChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Checkbox(
            checked = value == 1,
            onCheckedChange = { onChange(if (it) 1 else 0) },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF0F9D58)
            )
        )
        Text(label)
    }
}