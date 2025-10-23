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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.gihealth.ui.theme.GIHealthTheme

@Composable
fun JournalScreen() {
    var journalText by remember { mutableStateOf("") }
    var journalEntries by remember { mutableStateOf(listOf<JournalEntry>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Daily Journal", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        // Date navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Today",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Journal entry input
        OutlinedTextField(
            value = journalText,
            onValueChange = { journalText = it },
            label = { Text("How are you feeling today?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (journalText.isNotBlank()) {
                    val formatter = SimpleDateFormat("MMMM d',' yyyy", Locale.getDefault())
                    val date = formatter.format(Date())
                    val newEntry = JournalEntry(journalText, date)
                    journalEntries = listOf(newEntry) + journalEntries
                    journalText = ""
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Past entries
        Text("Past Entries", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(journalEntries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Put Date in top-right
                        Text(
                            text = entry.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // vertical space between date and text
                        Spacer(modifier = Modifier.height(8.dp))

                        // Journal text content
                        Text(
                            text = entry.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

data class JournalEntry(
    val text: String,
    val date: String
)


@Preview(showBackground = true)
@Composable
fun JournalScreenPreview() {
    GIHealthTheme {
        JournalScreen()
    }
}
