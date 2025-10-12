package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gihealth.ui.theme.GIHealthTheme

@Composable
fun JournalScreen() {
    var journalText by remember { mutableStateOf("") }

    // mock data
    val mockEntries = listOf(
        "Felt relaxed and calm today 🌤️",
        "Had a stressful afternoon but ended the day well.",
        "Not feeling great — some stomach pain in the evening."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Daily Journal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            //horizontalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.width(90.dp))
            // button to go back to previous days
            TextButton(
                onClick = { /* TODO: go to previous day */ },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "<",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Today",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Input field for journal entry
        OutlinedTextField(
            value = journalText,
            onValueChange = { journalText = it },
            label = { Text("How are you feeling today?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Save button (no functionality yet)
        Button(
            onClick = { /* TODO: implement later */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section title
        Text(
            text = "Past Entries",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mocked list of entries
        LazyColumn {
            items(mockEntries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = entry,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JournalScreenPreview() {
    GIHealthTheme {
        JournalScreen()
    }
}
