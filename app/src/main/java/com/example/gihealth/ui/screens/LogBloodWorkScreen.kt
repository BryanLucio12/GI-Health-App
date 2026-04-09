package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gihealth.ui.viewmodel.BloodworkViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.layout.padding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogBloodWorkScreen(navController: NavController) {
    val viewModel: BloodworkViewModel = viewModel()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

    var b12Text by remember { mutableStateOf("") }
    var crpText by remember { mutableStateOf("") }

    var b12Error by remember { mutableStateOf<String?>(null) }
    var crpError by remember { mutableStateOf<String?>(null) }

    fun isValidDecimal(input: String): Boolean {
        return input.matches(Regex("^\\d*\\.?\\d*$"))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Log Blood Work", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { selectedDate = selectedDate.minusDays(1) }
                    ) {
                        Text(
                            "<",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = if (selectedDate == LocalDate.now()) {
                            "Today (${selectedDate.format(dateFormatter)})"
                        } else {
                            selectedDate.format(dateFormatter)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    TextButton(
                        onClick = { selectedDate = selectedDate.plusDays(1) }
                    ) {
                        Text(
                            ">",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Text(
                    "Enter your lab values",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("B12 Level", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = b12Text,
                        onValueChange = { newValue ->
                            if (isValidDecimal(newValue)) {
                                b12Text = newValue
                                val numeric = newValue.toDoubleOrNull()

                                b12Error = when {
                                    newValue.isBlank() -> null
                                    numeric == null -> "Invalid number"
                                    numeric < 0 -> "Value cannot be negative"
                                    else -> null
                                }
                            }
                        },
                        label = { Text("B12") },
                        placeholder = { Text("Example: 450.0") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = b12Error != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    b12Error?.let {
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("CRP Level", fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = crpText,
                        onValueChange = { newValue ->
                            if (isValidDecimal(newValue)) {
                                crpText = newValue
                                val numeric = newValue.toDoubleOrNull()

                                crpError = when {
                                    newValue.isBlank() -> null
                                    numeric == null -> "Invalid number"
                                    numeric < 0 -> "Value cannot be negative"
                                    else -> null
                                }
                            }
                        },
                        label = { Text("CRP") },
                        placeholder = { Text("Example: 3.2") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = crpError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    crpError?.let {
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.insertBloodwork(
                            date = selectedDate.format(dateFormatter),
                            b12Level = b12Text.toDoubleOrNull(),
                            crpLevel = crpText.toDoubleOrNull()
                        )
                        navController.popBackStack()
                    },
                    enabled = b12Error == null &&
                            crpError == null &&
                            (b12Text.isNotBlank() || crpText.isNotBlank()),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F9D58)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        "Save Blood Work",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}