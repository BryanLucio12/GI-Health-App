package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gihealth.data.UserInfoViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSetupScreen(
    userInfoViewModel: UserInfoViewModel,
    onSetUpComplete: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var triggers by remember { mutableStateOf("") }

    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf("") }

    var showErrors by remember { mutableStateOf(false) }
    var showDobDialog by remember { mutableStateOf(false) }

    var expandedGender by remember { mutableStateOf(false) }
    val genderOptions = listOf("Male", "Female", "Non-binary", "Other", "Prefer not to say")

    var bloodTypeExpanded by remember { mutableStateOf(false) }
    val bloodTypeOptions = listOf("A+", "A−", "B+", "B−", "AB+", "AB−", "O+", "O−", "Not sure")

    val isNameValid = name.isNotBlank()
    val isWeightValid = weight.isNotBlank()
    val isDobValid = dob.isNotBlank()
    val isGenderValid = gender.isNotBlank()
    val isBloodTypeValid = bloodType.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Let's Get to Know You",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // NAME
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Your Name *") },
                isError = showErrors && !isNameValid,
                modifier = Modifier.fillMaxWidth()
            )
            if (showErrors && !isNameValid) {
                Text("Name is required", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // WEIGHT
            OutlinedTextField(
                value = weight,
                onValueChange = { input ->
                    if (input.all { it.isDigit() || it == '.' }) {
                        weight = input
                    }
                },
                label = { Text("Weight (lbs) *") },
                isError = showErrors && !isWeightValid,
                modifier = Modifier.fillMaxWidth()
            )
            if (showErrors && !isWeightValid) {
                Text("Weight is required", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DOB FIELD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDobDialog = true }
            ) {
                OutlinedTextField(
                    value = dob,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,   // important
                    label = { Text("Date of Birth *") },
                    isError = showErrors && !isDobValid,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showErrors && !isDobValid) {
                Text("Date of birth is required", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GENDER
            ExposedDropdownMenuBox(
                expanded = expandedGender,
                onExpandedChange = { expandedGender = !expandedGender }
            ) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gender *") },
                    isError = showErrors && !isGenderValid,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
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
            if (showErrors && !isGenderValid) {
                Text("Gender is required", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BLOOD TYPE
            ExposedDropdownMenuBox(
                expanded = bloodTypeExpanded,
                onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded }
            ) {
                OutlinedTextField(
                    value = bloodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Type *") },
                    isError = showErrors && !isBloodTypeValid,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = bloodTypeExpanded,
                    onDismissRequest = { bloodTypeExpanded = false }
                ) {
                    bloodTypeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                bloodType = option
                                bloodTypeExpanded = false
                            }
                        )
                    }
                }
            }
            if (showErrors && !isBloodTypeValid) {
                Text("Blood type is required", color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = triggers,
                onValueChange = { triggers = it },
                label = { Text("Known Triggers (optional)") },
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Continue button
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58)),
                onClick = {
                    showErrors = true
                    if (isNameValid &&
                        isWeightValid &&
                        isDobValid &&
                        isGenderValid &&
                        isBloodTypeValid
                    ) {
                        userInfoViewModel.saveUserInfo(
                            name = name,
                            age = 0,
                            bloodType = bloodType,
                            weight = weight.toFloatOrNull() ?: 0f,
                            gender = gender,
                            disease = "",
                            triggers = triggers,
                            dob = dob
                        )
                        //go to main app screen
                        onSetUpComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue", color = Color.White, fontSize = 18.sp)
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
fun DobDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val months = (1..12).map { it.toString().padStart(2, '0') }
    val days = (1..31).map { it.toString().padStart(2, '0') }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (1900..currentYear).map { it.toString() }.reversed()

    var selectedMonth by remember { mutableStateOf(months[0]) }
    var selectedDay by remember { mutableStateOf(days[0]) }
    var selectedYear by remember { mutableStateOf(years[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm("$selectedMonth/$selectedDay/$selectedYear")
                }
            ) {
                Text("Confirm", color = Color(0xFF0F9D58))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WheelColumn(months) { selectedMonth = it }
                WheelColumn(days) { selectedDay = it }
                WheelColumn(years) { selectedYear = it }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelColumn(
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    val itemHeight = 40.dp
    val visibleItemsCount = 3

    val listState = rememberLazyListState()

    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState
    )

    Box(
        modifier = Modifier
            .width(90.dp)
            .height(itemHeight * visibleItemsCount)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(
                vertical = itemHeight
            )
        ) {
            items(items.size) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Center highlight
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .height(itemHeight)
                .fillMaxWidth()
                .border(1.dp, Color(0xFF0F9D58))
        )
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val index = listState.firstVisibleItemIndex
        if (index in items.indices) {
            onItemSelected(items[index])
        }
    }
}