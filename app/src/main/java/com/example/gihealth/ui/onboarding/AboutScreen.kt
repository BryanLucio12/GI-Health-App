package com.example.gihealth.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.offset(x = (-12).dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "About GI Health",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Belly Balance",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F9D58)
            )
            
            Text(
                text = "Track. Reflect. Improve.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AboutSection(
                title = "Our Mission",
                content = "GI Health is designed to empower individuals with " +
                        "Gastrointestinal (GI) concerns to take control of their well-being." +
                        " By providing intuitive tracking for meals, symptoms, and daily habits, " +
                        "we aim to help you identify patterns and make informed" +
                        " decisions about your health."
            )

            AboutSection(
                title = "Key Features",
                content = "• Food & Ingredient Logging\n• " +
                        "Symptom Severity Tracking\n• " +
                        "Daily Personal Journaling\n• " +
                        "Visual Analytics & Trends\n• Secure PIN Protection"
            )

            AboutSection(
                title = "Privacy",
                content = "Your health data is personal. " +
                        "GI Health stores all your information locally on your device. " +
                        "We do not upload your health logs to any external servers without your " +
                        "explicit action (like generating a PDF report)."
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            val footerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            
            Text(
                text = "Version 1.0.0",
                fontSize = 14.sp,
                color = footerColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "© 2025 GI Health Team",
                fontSize = 14.sp,
                color = footerColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AboutSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Justify
        )
    }
}
