package com.example.gihealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.Modifier
import com.example.gihealth.ui.theme.GIHealthTheme
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gihealth.ui.screens.*
import com.example.gihealth.utils.Constants
import com.example.gihealth.ui.onboarding.CreatePinScreen
import com.example.gihealth.ui.onboarding.EnterPinScreen
import com.example.gihealth.ui.onboarding.ForgotPinScreen
import com.example.gihealth.ui.onboarding.UserSetupScreen

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
fun AppNavigator(){
    val navController = rememberNavController()

    var hasPin by remember { mutableStateOf(false) }
    var userSetUpFinished by remember { mutableStateOf(false) }

    val startDestination = when{
        !hasPin -> "create_pin"
        else -> "enter_pin"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable("create_pin"){
            CreatePinScreen(
                navController = navController,
                onPinCreated = {
                    hasPin = true
                    navController.navigate("user_setup"){
                        popUpTo("create_pin"){ inclusive = true }
                    }

                }
            )
        }

        composable("enter_pin"){
            EnterPinScreen(
                navController = navController,
                loginSuccess = {
                    if(userSetUpFinished){
                        navController.navigate("main_app"){
                            popUpTo("enter_pin") {inclusive = true}
                        }
                    }
                    else{
                        navController.navigate("user_setup"){
                            popUpTo("enter_pin") {inclusive = true}
                        }
                    }
                }
            )
        }

        composable("user_setup"){
            UserSetupScreen(
                onSetUpComplete = {
                    userSetUpFinished = true
                    navController.navigate("main_app"){
                        popUpTo("user_setup") {inclusive = true}
                    }
                }
            )
        }

        composable("forgot_pin"){
            ForgotPinScreen(navController = navController)
        }
        composable("main_app"){
            MainNavHost()
        }
    }
}

@Composable
fun MainNavHost(){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = {padding ->
            NavHostContainer(
                navController = navController,
                padding = padding
            )
        }
    )
}
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,

        // set the start destination as food screen
        startDestination = "food",

        // Set the padding provided by scaffold
        modifier = Modifier.padding(paddingValues = padding),

        builder = {
            // route : go to food screen
            composable("food") {
                FoodScreen(navController)
            }
            // route : go to symptom screen
            composable("symptoms") {
                SymptomScreen()
            }
            // route : go to Add New screen
            composable("add") {
                AddNewScreen(navController)
            }
            // route : go to Journal screen
            composable("journal") {
                JournalScreen()
            }
            // route : go to Analytics screen
            composable("analytics") {
                AnalyticsScreen()
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
        })
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        // set background color of the bottom bar
        containerColor = Color(0xFF0F9D58)) {

        // observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // observe current route to change the icon
        // color,label color when navigated
        val currentRoute = navBackStackEntry?.destination?.route

        // Bottom nav items we declared
        Constants.BottomNavItems.forEach { navItem ->

            // Place the bottom nav items
            NavigationBarItem(

                // it currentRoute is equal then its selected route
                selected = currentRoute == navItem.route,

                // navigate on click
                onClick = {
                    navController.navigate(navItem.route)
                },

                // Icon of navItem
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                },

                // label
                label = {
                    Text(text = navItem.label)
                },
                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White, // Icon color when selected
                    unselectedIconColor = Color.White, // Icon color when not selected
                    selectedTextColor = Color.White, // Label color when selected
                    indicatorColor = Color(0xFF195334) // Highlight color for selected item
                )
            )
        }
    }
}