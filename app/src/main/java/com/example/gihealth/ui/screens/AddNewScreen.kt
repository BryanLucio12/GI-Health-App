package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun AddNewScreen(navController: NavHostController) {
    val cardColor = Color(0xFF0F9D58)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title at the top
        Text(
            text = "Add New Entry",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Grid area centered on screen
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // First row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AddNewCard(
                    title = "Log Food",
                    icon = Icons.Default.Dining,
                    backgroundColor = cardColor,
                    onClick = { navController.navigate("logFood") }
                )
                AddNewCard(
                    title = "Log Symptom",
                    icon = Icons.Default.Sick,
                    backgroundColor = cardColor,
                    onClick = { navController.navigate("select_symptoms") }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Second row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AddNewCard(
                    title = "Add to Journal",
                    icon = Icons.Default.AutoStories,
                    backgroundColor = cardColor,
                    onClick = { navController.navigate("journal") }
                )
                AddNewCard(
                    title = "Log Wellbeing",
                    icon = Icons.Default.MonitorWeight,
                    backgroundColor = cardColor,
                    onClick = { navController.navigate("logWeight") }
                )
            }
        }
    }
}

@Composable
fun AddNewCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(170.dp) // 🔹 slightly bigger cards
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
