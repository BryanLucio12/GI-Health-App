package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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

    // ViewModel
    val userInfoViewModel: UserInfoViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    // User Data
    val userInfo by userInfoViewModel.userInfo.observeAsState()

    // Track edit mode
    var isEditing by remember { mutableStateOf(false) }

    // Editable fields
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }
    var triggers by remember { mutableStateOf("") }

    // When first loading OR toggling edit mode on:
    LaunchedEffect(userInfo, isEditing) {
        if (userInfo != null && !isEditing) {
            name = userInfo!!.name
            age = userInfo!!.age.toString()
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

        Text(
            text = "Profile",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(30.dp))


        if (!isEditing) {
            ProfileField(label = "Name", value = userInfo?.name ?: "")
            ProfileField(label = "Age", value = userInfo?.age?.toString() ?: "")
            ProfileField(label = "Weight", value = userInfo?.weight?.toString() ?: "")
            ProfileField(label = "Gender", value = userInfo?.gender ?: "")
            ProfileField(label = "Blood Type", value = userInfo?.bloodType ?: "")
            ProfileField(label = "Conditions", value = userInfo?.disease ?: "")
            ProfileField(label = "Triggers", value = userInfo?.triggers ?: "")

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { isEditing = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile", color = Color.White)
            }
        }
        else {


            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = { age = it.filter { c -> c.isDigit() } },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Weight
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Weight (lbs)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            //Gender drop down
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            //Blood type dropdown
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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodType) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
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

            // Disease
            OutlinedTextField(
                value = disease,
                onValueChange = { disease = it },
                label = { Text("Conditions") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Triggers
            OutlinedTextField(
                value = triggers,
                onValueChange = { triggers = it },
                label = { Text("Triggers") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        userInfoViewModel.saveUserInfo(
                            name = name,
                            age = age.toIntOrNull() ?: 0,
                            bloodType = bloodType,
                            weight = weight.toFloatOrNull() ?: 0f,
                            gender = gender,
                            disease = disease,
                            triggers = triggers
                        )
                        isEditing = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
                ) {
                    Text("Save", color = Color.White)
                }

                OutlinedButton(
                    onClick = { isEditing = false }
                ) {
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