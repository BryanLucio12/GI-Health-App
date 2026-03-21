package com.example.gihealth.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.work.*
import androidx.core.content.edit
import com.example.gihealth.utils.NotificationHelper
import com.example.gihealth.utils.NotificationWorker
import java.util.concurrent.TimeUnit

@Composable
fun SettingsScreen(navController: NavController, rootNavController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("gi_health_settings", Context.MODE_PRIVATE)
    }

    var notificationsEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", false))
    }

    var lockRotationEnabled by remember {
        mutableStateOf(sharedPreferences.getBoolean("lock_rotation_enabled", false))
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationsEnabled = true
            sharedPreferences.edit { putBoolean("notifications_enabled", true) }
            NotificationHelper.createNotificationChannel(context)
            scheduleDailyReminder(context)
        } else {
            notificationsEnabled = false
            sharedPreferences.edit { putBoolean("notifications_enabled", false) }
        }
    }

    Scaffold(
        topBar = {
            Text(
                text = "Settings",
                modifier = Modifier.padding(16.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                SettingsSectionTitle("Notifications")
                SettingsToggleItem(
                    title = "Daily Reminders",
                    description = "Get a daily reminder to log your health data",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
                                    PackageManager.PERMISSION_GRANTED -> {
                                        notificationsEnabled = true
                                        sharedPreferences.edit { putBoolean("notifications_enabled", true) }
                                        NotificationHelper.createNotificationChannel(context)
                                        scheduleDailyReminder(context)
                                    }
                                    else -> {
                                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                notificationsEnabled = true
                                sharedPreferences.edit { putBoolean("notifications_enabled", true) }
                                NotificationHelper.createNotificationChannel(context)
                                scheduleDailyReminder(context)
                            }
                        } else {
                            notificationsEnabled = false
                            sharedPreferences.edit { putBoolean("notifications_enabled", false) }
                            cancelDailyReminder(context)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionTitle("Display & Orientation")
                SettingsToggleItem(
                    title = "Lock Rotation",
                    description = "Keep the app in portrait mode",
                    icon = Icons.Default.ScreenLockPortrait,
                    checked = lockRotationEnabled,
                    onCheckedChange = { enabled ->
                        lockRotationEnabled = enabled
                        sharedPreferences.edit { putBoolean("lock_rotation_enabled", enabled) }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionTitle("Privacy & Security")
                SettingsClickableItem(
                    title = "Change PIN",
                    description = "Update your 4-digit security PIN",
                    icon = Icons.Default.Lock,
                    onClick = {
                        rootNavController.navigate("create_pin?reset=true")
                    }
                )
                SettingsClickableItem(
                    title = "Security Questions",
                    description = "Update your security recovery questions",
                    icon = Icons.Default.PrivacyTip,
                    onClick = {
                        rootNavController.navigate("questionnaire")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingsSectionTitle("About")
                SettingsClickableItem(
                    title = "About GI Health",
                    description = "Learn more about Belly Balance",
                    icon = Icons.Default.Info,
                    onClick = {
                        navController.navigate("about")
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = Color(0xFF0F9D58),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    description: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0F9D58)
            )
        )
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
    }
}

fun scheduleDailyReminder(context: Context) {
    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
        .setInputData(workDataOf(
            "title" to "Daily Reminder",
            "message" to "Time to log your GI health data for today!",
            "id" to 101
        ))
        .setInitialDelay(1, TimeUnit.HOURS)
        .addTag("daily_reminder")
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_reminder",
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )
}

fun cancelDailyReminder(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
}
