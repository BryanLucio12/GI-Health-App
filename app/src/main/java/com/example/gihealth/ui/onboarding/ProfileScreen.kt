package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.example.gihealth.data.WellBeingViewModel
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
import androidx.compose.runtime.livedata.observeAsState
import com.example.gihealth.data.UserInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current

    val userInfoViewModel: UserInfoViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    val wellBeingViewModel: WellBeingViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    val wellBeingEntries by wellBeingViewModel.entries.observeAsState(emptyList())
    val latestWeight = wellBeingEntries.maxByOrNull { it.timestamp }?.weight

    val userInfo by userInfoViewModel.userInfo.observeAsState()

    var isEditing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Editable fields
    var name by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }
    var triggers by remember { mutableStateOf("") }

    LaunchedEffect(userInfo, isEditing) {
        if (userInfo != null && !isEditing) {
            name = userInfo!!.name
            dob = userInfo!!.dob
            weight = userInfo!!.weight.toString()
            gender = userInfo!!.gender
            bloodType = userInfo!!.bloodType
            disease = userInfo!!.disease
            triggers = userInfo!!.triggers
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        if (!isEditing) {

            ProfileField("Name", userInfo?.name ?: "")
            ProfileField("Date of Birth", userInfo?.dob ?: "—")

            ProfileField(
                "Weight",
                latestWeight?.let { "${it.toInt()} lbs" }
                    ?: userInfo?.weight?.toString()
                    ?: "—"
            )

            ProfileField("Gender", userInfo?.gender ?: "")
            ProfileField("Blood Type", userInfo?.bloodType ?: "")
            ProfileField("Conditions", userInfo?.disease ?: "")
            ProfileField("Triggers", userInfo?.triggers ?: "")

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { isEditing = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile", color = Color.White)
            }

        } else {

            // NAME
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                isError = name.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // DOB
            OutlinedTextField(
                value = dob,
                onValueChange = { if (it.length <= 10) dob = it },
                label = { Text("Date of Birth (MM/DD/YYYY)") },
                isError = dob.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // WEIGHT
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Weight (lbs)") },
                isError = weight.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // GENDER
            var expandedGender by remember { mutableStateOf(false) }
            val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")

            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender") },
                    isError = gender.isBlank(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedGender,
                    onDismissRequest = { expandedGender = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                expandedGender = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BLOOD TYPE
            var expandedBloodType by remember { mutableStateOf(false) }
            val bloodTypeOptions = listOf(
                "A+", "A−", "B+", "B−", "AB+", "AB−", "O+", "O−", "Not sure"
            )

            ExposedDropdownMenuBox(
                expanded = expandedBloodType,
                onExpandedChange = { expandedBloodType = !expandedBloodType }
            ) {
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Type") },
                    isError = bloodType.isBlank(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodType) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedBloodType,
                    onDismissRequest = { expandedBloodType = false }
                ) {
                    bloodTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                bloodType = option
                                expandedBloodType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // CONDITIONS
            OutlinedTextField(
                value = disease,
                onValueChange = { disease = it },
                label = { Text("Conditions") },
                isError = disease.isBlank(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // TRIGGERS (OPTIONAL)
            OutlinedTextField(
                value = triggers,
                onValueChange = { triggers = it },
                label = { Text("Triggers (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {

                        if (
                            name.isBlank() ||
                            dob.isBlank() ||
                            weight.isBlank() ||
                            gender.isBlank() ||
                            bloodType.isBlank() ||
                            disease.isBlank()
                        ) {
                            errorMessage = "Please complete all required fields."
                            return@Button
                        }

                        errorMessage = ""

                        userInfoViewModel.saveUserInfo(
                            name = name,
                            age = 0,
                            bloodType = bloodType,
                            weight = weight.toFloatOrNull() ?: 0f,
                            gender = gender,
                            disease = disease,
                            triggers = triggers,
                            dob = dob
                        )

                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
                ) {
                    Text("Save", color = Color.White)
                }

                OutlinedButton(onClick = { isEditing = false }) {
                    Text("Cancel")
                }
            }
        }
    }
}


@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold)
        Text(text = value.ifBlank { "—" })
        Spacer(modifier = Modifier.height(12.dp))
    }
}