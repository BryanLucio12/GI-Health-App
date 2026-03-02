package com.example.gihealth.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.ui.viewmodel.FoodHistoryViewModel
import com.example.gihealth.ui.viewmodel.FoodHistoryViewModelFactory
import com.example.gihealth.data.*

@Composable
fun FoodHistoryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val dao = FoodDatabase.getDatabase(context).foodDao()

    val vm: FoodHistoryViewModel = viewModel(
        factory = FoodHistoryViewModelFactory(dao)
    )

    var search by remember { mutableStateOf("") }

    val filtered = vm.foods.filter {
        it.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Search Food History",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search foods") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { food ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.selectFood(food) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = vm.selectedFood == food,
                        onClick = { vm.selectFood(food) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0F9D58)
                        )
                    )

                    Spacer(Modifier.width(8.dp))
                    Text(food)
                }
            }
        }

        vm.lastLoggedDate?.let { date ->

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Last Logged: $date",
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("calendar/$date")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58),
                    contentColor = Color.White
                )
            ) {
                Text("Go to That Day")
            }
        }
    }
}