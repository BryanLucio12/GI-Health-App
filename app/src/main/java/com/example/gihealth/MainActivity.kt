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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GIHealthTheme {
                val navController = rememberNavController()
                Surface(color = Color.White) {
                    //Scaffold Component
                    Scaffold(
                        //Bottom navigation bar
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        },
                        content = { padding ->
                            //Main Navigation Container
                            NavHostContainer(
                                navController = navController,
                                padding = padding
                            )
                        }
                    )
                }
            }
        }
    }
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
                FoodScreen()
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

