package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gihealth.ui.theme.GIHealthTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun JournalScreen() {
    var journalText by remember { mutableStateOf("") }
    var journalEntries by remember { mutableStateOf(listOf<JournalEntry>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Match Analytics screen style
        Text(
            text = "Daily Journal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Centered "Today"
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
                    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                    val date = formatter.format(Date())
                    val newEntry = JournalEntry(journalText.trim(), date)
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
        Text("Past Entries",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))

        // Make list take remaining space so it can scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(journalEntries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        // Date in top-right
                        Text(
                            text = entry.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        // Journal text content
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 20.dp)
                        ) {
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
