package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gihealth.data.JournalViewModel
import com.example.gihealth.ui.theme.GIHealthTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun JournalScreen(
    journalViewModel: JournalViewModel
) {
    var journalText by remember { mutableStateOf("") }
    val journalEntries by journalViewModel.journalEntries.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    }

    val dbDateFormatter = remember {
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())
    }

    val selectedDbDateString = remember(selectedDate) {
        selectedDate.format(dbDateFormatter)
    }

    val entriesForSelectedDate = remember(journalEntries, selectedDbDateString) {
        journalEntries.filter { it.date == selectedDbDateString }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daily Journal",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                Text("<", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
            }

            Spacer(Modifier.width(6.dp))

            Text(
                text = if (selectedDate == LocalDate.now())
                    "Today (${selectedDate.format(dateFormatter)})"
                else selectedDate.format(dateFormatter),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            Spacer(Modifier.width(6.dp))

            TextButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                Text(">", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    journalViewModel.addJournalEntry(
                        text = journalText.trim(),
                        date = selectedDate
                    )
                    journalText = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F9D58),
                contentColor = Color.White
            )
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Entries for ${selectedDate.format(dateFormatter)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(entriesForSelectedDate) { entry ->
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
                        Text(
                            text = entry.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(top = 20.dp)
                        ) {
                            Text(
                                text = entry.entry,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

