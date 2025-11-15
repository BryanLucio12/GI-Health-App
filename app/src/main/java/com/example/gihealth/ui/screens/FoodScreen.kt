package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun FoodScreen(
    navController: NavHostController,
    mealLogs: List<Map<String, String>>
) {
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Food",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Track your meals and ingredients",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("logFood") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(45.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Food",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Add Food", color = Color.White, fontSize = 16.sp)
            }
        }

        items(mealTypes) { type ->
            MealCard(
                label = type,
                logs = mealLogs.filter { it["meal"] == type }
            )
        }
    }
}

@Composable
fun MealCard(label: String, logs: List<Map<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            if (logs.isEmpty()) {
                Text(
                    text = "No items logged yet.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                logs.forEach { log ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = log["food"] ?: "",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${log["time"]}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
