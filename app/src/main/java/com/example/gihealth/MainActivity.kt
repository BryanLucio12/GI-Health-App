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
import com.example.gihealth.ui.logroutes.LogFoodRoute
import com.example.gihealth.ui.theme.GIHealthTheme
import com.example.gihealth.utils.Constants
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.gihealth.data.UserInfoViewModel
import com.example.gihealth.data.JournalViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.Composable
import com.example.gihealth.ui.viewmodel.FoodViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

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
    val context = LocalContext.current
    // Initialize the userinfo viewmodel to access userinfo db
    val userInfoViewModel: UserInfoViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as android.app.Application)
        )


        //look at userinfo livedata to know if account already set up
    val userInfo by userInfoViewModel.userInfo.observeAsState()
    //if pin already created
    val hasPin = userInfo?.pin?.let { it != 0 } ?: false
    //if user has already completed setup name required
    val hasSetupCompleted = userInfo?.name?.isNotBlank() == true
    //determine the first screen shown based on pin and setup status
    val savedPin = userInfo?.pin?.toString() ?: ""
    val startDestination = when {
        !hasPin -> "create_pin"
        !hasSetupCompleted -> "user_setup"
        else -> "enter_pin"
    }


    NavHost(navController = navController, startDestination = startDestination) {


        // ➡️ Create PIN
        composable("create_pin") {
            CreatePinScreen(
                navController = navController,
                onPinCreated = {

                    navController.navigate("user_setup") {
                        popUpTo("create_pin") { inclusive = true }
                    }
                }
            )
        }

        // ➡️ Enter PIN
        composable("enter_pin") {

            EnterPinScreen(
                savedPin = savedPin,
                navController = navController,
                loginSuccess = {
                    if (userInfo?.name?.isNotBlank()==true) {
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
                userInfoViewModel=userInfoViewModel, //pass viewmodel to save user info
                onSetUpComplete = {

                    navController.navigate("main_app") {
                        popUpTo("user_setup") { inclusive = true }
                    }
                }
            )
        }

        // ➡️ Forgot PIN
        composable("forgot_pin") {
            ForgotPinScreen(
                onNewPinSaved = { newPin ->
                    // Reuse existing saveUserPin function
                    userInfoViewModel.saveUserPin(newPin.toInt())

                    // Navigate back to Enter PIN
                    navController.navigate("enter_pin") {
                        popUpTo("forgot_pin") { inclusive = true }
                    }
                }
            )
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

// In NavHostContainer(...)
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    val vm: CalendarViewModel = viewModel()
    val symptomViewModel: SymptomViewModel = viewModel()
    val foodVm: FoodViewModel = viewModel()

    val todayFoods by foodVm.todayFoods.observeAsState(emptyList())

    NavHost(
        navController = navController,
        startDestination = "add",
        modifier = Modifier.padding(padding)
    ) {
        composable("food") {

            val mealLogs = todayFoods.map { entity ->
                mapOf(
                    "food" to entity.name,
                    "time" to entity.time,
                    "meal" to entity.meal,
                    "ingredients" to entity.ingredients,
                    "date" to entity.date
                )
            }


            FoodScreen(
                navController = navController,
                mealLogs = mealLogs
            )
        }

        composable("logFood") {
            LogFoodScreen(
                foodViewModel = foodVm,
                onSave = { food, time, meal, ingredients, date ->
                    foodVm.insertFood(
                        name = food,
                        time = time,
                        meal = meal,
                        ingredients = ingredients,
                        date = date
                    )
                    navController.popBackStack()
                },
                onBackPressed = { navController.popBackStack() }
            )
        }


        composable("symptoms") {
            SymptomScreen(navController = navController, vm = symptomViewModel)
        }

        composable("add") {
            AddNewScreen(navController)
        }

        composable("journal") {
            JournalScreen()
        }

        composable("logSymptom") {
            LogSymptomScreen(
                navController = navController,
                symptomViewModel = symptomViewModel
            )
        }

        composable("logWeight") {
            LogWeightScreen(navController)
        }

        composable("analytics") {
            AnalyticsScreen(
                onOpenCalendar = { navController.navigate("calendar") },
                vm = vm
            )
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
