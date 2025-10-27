package com.example.gihealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gihealth.ui.onboarding.CreatePinScreen
import com.example.gihealth.ui.onboarding.EnterPinScreen
import com.example.gihealth.ui.onboarding.ForgotPinScreen
import com.example.gihealth.ui.onboarding.UserSetupScreen
import com.example.gihealth.ui.screens.*
import com.example.gihealth.ui.theme.GIHealthTheme
import com.example.gihealth.utils.Constants
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GIHealthTheme {
                Surface(color = Color.White) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    var hasPin by remember { mutableStateOf(false) }
    var userSetUpFinished by remember { mutableStateOf(false) }

    val startDestination = if (!hasPin) "create_pin" else "enter_pin"

    NavHost(navController = navController, startDestination = startDestination) {

        // ➡️ Create PIN
        composable("create_pin") {
            CreatePinScreen(
                navController = navController,
                onPinCreated = {
                    hasPin = true
                    navController.navigate("user_setup") {
                        popUpTo("create_pin") { inclusive = true }
                    }
                }
            )
        }

        // ➡️ Enter PIN
        composable("enter_pin") {
            EnterPinScreen(
                navController = navController,
                loginSuccess = {
                    if (userSetUpFinished) {
                        navController.navigate("main_app") {
                            popUpTo("enter_pin") { inclusive = true }
                        }
                    } else {
                        navController.navigate("user_setup") {
                            popUpTo("enter_pin") { inclusive = true }
                        }
                    }
                }
            )
        }

        // ➡️ User Setup
        composable("user_setup") {
            UserSetupScreen(
                onSetUpComplete = {
                    userSetUpFinished = true
                    navController.navigate("main_app") {
                        popUpTo("user_setup") { inclusive = true }
                    }
                }
            )
        }

        // ➡️ Forgot PIN
        composable("forgot_pin") {
            ForgotPinScreen(navController = navController)
        }

        // ➡️ Main App
        composable("main_app") {
            MainNavHost()
        }
    }
}

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->
        NavHostContainer(
            navController = navController,
            padding = padding
        )
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    val vm: CalendarViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(
        navController = navController,
        startDestination = "food",
        modifier = Modifier.padding(padding)
    )
    {
        composable("food")     {
            FoodScreen(navController)
        }
        composable("symptoms") {
            SymptomScreen()
        }
        composable("add")      {
            AddNewScreen(navController)
        }
        composable("journal")  {
            JournalScreen()
        }
        composable("logFood") {
            LogFoodScreen()
        }
        composable("logSymptom") {
            LogSymptomScreen()
        }
        composable("logWeight") {
            LogWeightScreen()
        }

        composable("analytics") {
            AnalyticsScreen(
                onOpenCalendar = { navController.navigate("calendar") },
                vm = vm
            )
        }

        composable("logFood") {
            LogFoodScreen()
        }

        composable("logSymptom") {
            LogSymptomScreen()
        }

        composable("logWeight") {
            LogWeightScreen()
        }

        composable("calendar") {
            FullCalendarScreen(
                onClose = { navController.popBackStack() },
                vm = vm
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFF0F9D58)) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Constants.BottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color(0xFF195334)
                )
            )
        }
    }
}
