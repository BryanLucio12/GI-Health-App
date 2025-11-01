package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun CreatePinScreen(navController: NavController, onPinCreated: (String) -> Unit) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "Belly Balance",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Track. Reflect. Improve.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create Your PIN",
                fontSize = 23.sp,
                fontWeight = FontWeight.SemiBold,
            )

            OutlinedTextField(
                value = pin,
                //Pin is 4 digits in length
                onValueChange = {
                    if (it.length <= 4)
                        pin = it
                },
                label = {
                    Text("Enter 4-digit PIN")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPin,
                onValueChange = {
                    //Length of pin is 4
                    if (it.length <= 4)
                        confirmPin = it
                },
                label = {
                    Text("Confirm PIN")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            //Design of the error message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }


            //Confirm Pin
            Button(
                onClick = {
                    when {
                        pin.length != 4 || confirmPin.length != 4 ->
                            errorMessage = "PIN must be 4 digits"
                        pin != confirmPin ->
                            errorMessage = "PINs do not match"
                        else -> {
                            errorMessage = ""
                            onPinCreated(pin)
                            navController.navigate("enter_pin") {
                                popUpTo("create_pin") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm",
                    color = Color.White
                )
            }
        }
    }
}
