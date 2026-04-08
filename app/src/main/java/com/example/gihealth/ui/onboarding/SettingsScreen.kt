package com.example.gihealth.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.work.*
import androidx.core.content.edit
import com.example.gihealth.utils.NotificationHelper
import com.example.gihealth.utils.NotificationWorker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
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

    var reminderHour by remember {
        mutableIntStateOf(sharedPreferences.getInt("reminder_hour", 9))
    }
    var reminderMinute by remember {
        mutableIntStateOf(sharedPreferences.getInt("reminder_minute", 0))
    }

    var showTimePickerDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationsEnabled = true
            sharedPreferences.edit { putBoolean("notifications_enabled", true) }
            NotificationHelper.createNotificationChannel(context)
            scheduleDailyReminder(context, reminderHour, reminderMinute)
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
                                        scheduleDailyReminder(context, reminderHour, reminderMinute)
                                    }
                                    else -> {
                                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            } else {
                                notificationsEnabled = true
                                sharedPreferences.edit { putBoolean("notifications_enabled", true) }
                                NotificationHelper.createNotificationChannel(context)
                                scheduleDailyReminder(context, reminderHour, reminderMinute)
                            }
                        } else {
                            notificationsEnabled = false
                            sharedPreferences.edit { putBoolean("notifications_enabled", false) }
                            cancelDailyReminder(context)
                        }
                    }
                )

                if (notificationsEnabled) {
                    SettingsClickableItem(
                        title = "Reminder Time",
                        description = "Current time: ${formatTime(reminderHour, reminderMinute)}",
                        icon = Icons.Default.AccessTime,
                        onClick = {
                            showTimePickerDialog = true
                        }
                    )
                    
                    SettingsClickableItem(
                        title = "Send Test Notification",
                        description = "Verify notifications are working",
                        icon = Icons.AutoMirrored.Filled.Send,
                        onClick = {
                            NotificationHelper.showNotification(
                                context,
                                "Test Notification",
                                "This is a test to verify your settings are correct!",
                                999
                            )
                        }
                    )
                }
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

    if (showTimePickerDialog) {
        ScrollTimePickerDialog(
            initialHour = reminderHour,
            initialMinute = reminderMinute,
            onDismiss = { showTimePickerDialog = false },
            onConfirm = { h, m ->
                reminderHour = h
                reminderMinute = m
                sharedPreferences.edit {
                    putInt("reminder_hour", h)
                    putInt("reminder_minute", m)
                }
                scheduleDailyReminder(context, h, m)
                showTimePickerDialog = false
            }
        )
    }
}

@Composable
fun ScrollTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val hours = (1..12).map { it.toString() }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }
    val amPm = listOf("AM", "PM")

    var selectedHour by remember { mutableStateOf((if (initialHour % 12 == 0) 12 else initialHour % 12).toString()) }
    var selectedMinute by remember { mutableStateOf(initialMinute.toString().padStart(2, '0')) }
    var selectedAmPm by remember { mutableStateOf(if (initialHour < 12) "AM" else "PM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time", fontWeight = FontWeight.Bold) },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsWheelColumn(hours, selectedHour) { selectedHour = it }
                Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                SettingsWheelColumn(minutes, selectedMinute) { selectedMinute = it }
                SettingsWheelColumn(amPm, selectedAmPm) { selectedAmPm = it }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val hourInt = selectedHour.toInt()
                    val minuteInt = selectedMinute.toInt()
                    val isPm = selectedAmPm == "PM"
                    
                    val h24 = when {
                        !isPm && hourInt == 12 -> 0
                        isPm && hourInt == 12 -> 12
                        isPm -> hourInt + 12
                        else -> hourInt
                    }
                    onConfirm(h24, minuteInt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58))
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsWheelColumn(
    items: List<String>,
    initialSelected: String,
    onItemSelected: (String) -> Unit
) {
    val itemHeight = 40.dp
    val visibleItemsCount = 3
    
    val initialIndex = items.indexOf(initialSelected).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    Box(
        modifier = Modifier
            .width(80.dp)
            .height(itemHeight * visibleItemsCount)
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight),
            horizontalAlignment = Alignment.CenterHorizontally
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
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
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
                .border(1.dp, Color(0xFF0F9D58), shape = MaterialTheme.shapes.small)
        )
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index in items.indices) {
                    onItemSelected(items[index])
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

fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
    val calendar = Calendar.getInstance()
    val now = Calendar.getInstance()
    
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    
    // If the time has already passed today, schedule for tomorrow
    if (calendar.before(now)) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }

    val delay = calendar.timeInMillis - now.timeInMillis

    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
        .setInputData(workDataOf(
            "title" to "Daily Reminder",
            "message" to "Time to log your GI health data for today!",
            "id" to 101
        ))
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .addTag("daily_reminder")
        .setConstraints(Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build())
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_reminder",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

fun cancelDailyReminder(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("daily_reminder")
}

private fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}
