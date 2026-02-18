package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.gihealth.data.WellBeingViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current

    val userInfoViewModel: UserInfoViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )


    val wellBeingViewModel: WellBeingViewModel = viewModel()

    val wellBeingEntries by wellBeingViewModel.entries.observeAsState(emptyList())

    val latestWeight = wellBeingEntries
        .sortedByDescending { it.timestamp }
        .firstOrNull()
        ?.weight

    val userInfo by userInfoViewModel.userInfo.observeAsState()

    var isEditing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showDobDialog by remember { mutableStateOf(false) }

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
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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
                    ?: userInfo?.weight?.let { "${it.toInt()} lbs" }
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // DOB BOX
            Text(
                text = "Date of Birth *",
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        1.dp,
                        if (dob.isBlank()) Color.Red else Color.DarkGray,
                        MaterialTheme.shapes.small
                    )
                    .clickable { showDobDialog = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = dob.ifBlank { "MM/DD/YYYY" },
                    color = if (dob.isBlank()) Color.Gray else Color.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = {
                    if (it.all { c -> c.isDigit() || c == '.' }) weight = it
                },
                label = { Text("Weight (lbs) *") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = disease,
                onValueChange = { disease = it },
                label = { Text("Conditions") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = triggers,
                onValueChange = { triggers = it },
                label = { Text("Triggers (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(
                onClick = {

                    if (
                        name.isBlank() ||
                        dob.isBlank() ||
                        weight.isBlank()
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save", color = Color.White)
            }
        }
    }

    if (showDobDialog) {
        DobDialog(
            onDismiss = { showDobDialog = false },
            onConfirm = {
                dob = it
                showDobDialog = false
            }
        )
    }
}
@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value.ifBlank { "—" }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}