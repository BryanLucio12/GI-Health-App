package com.example.gihealth

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gihealth.ui.onboarding.CreatePinScreen
import com.example.gihealth.ui.onboarding.EnterPinScreen
import com.example.gihealth.ui.onboarding.ForgotPinScreen
import com.example.gihealth.ui.onboarding.ProfileScreen
import com.example.gihealth.ui.onboarding.QuestionnaireScreen
import com.example.gihealth.ui.onboarding.QuestionnaireVerifyScreen
import com.example.gihealth.ui.onboarding.SettingsScreen
import com.example.gihealth.ui.onboarding.UserSetupScreen
import com.example.gihealth.ui.screens.*
import com.example.gihealth.ui.theme.GIHealthTheme
import com.example.gihealth.utils.Constants
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.data.UserInfoViewModel
import com.example.gihealth.data.JournalViewModel
import com.example.gihealth.data.FoodDatabase
import com.example.gihealth.data.FoodEntity
import com.example.gihealth.ui.viewmodel.FoodViewModel
import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
        viewModel(
            factory = ViewModelProvider.AndroidViewModelFactory(
                context.applicationContext as Application
            )
        )

    // look at userinfo livedata to know if account already set up
    val userInfo by userInfoViewModel.userInfo.observeAsState()

    // Show a loading screen while userInfo is null
    /*if (userInfo == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp)) // small gap
            Text(text = "Loading...")
        }
        return
    }*/
    // if pin already created
    val hasPin = userInfo?.pin?.let { it != 0 } ?: false
    // if user has already completed setup name required
    val hasSetupCompleted = userInfo?.name?.isNotBlank() == true
    // determine the first screen shown based on pin and setup status
    val savedPin = userInfo?.pin?.toString() ?: ""
    val startDestination = when {
        !hasPin -> "create_pin?reset=false"
        !hasSetupCompleted -> "user_setup"
        else -> "enter_pin"
    }

    NavHost(navController = navController, startDestination = startDestination) {

        // ➡️ Create PIN
        composable("create_pin?reset={reset}") { backStackEntry ->
            val reset = backStackEntry.arguments?.getString("reset") == "true"

            CreatePinScreen(
                navController = navController,
                onPinCreated = {
                    if (reset) {
                        // reset the pin
                        navController.navigate("enter_pin") {
                            popUpTo("create_pin") { inclusive = true }
                        }
                    } else {
                        // Questions asked for first time user
                        navController.navigate("questionnaire") {
                            popUpTo("create_pin") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("questionnaire") {
            QuestionnaireScreen(
                navController = navController,
                onComplete = {
                    navController.navigate("user_setup") {
                        popUpTo("questionnaire") { inclusive = true }
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
                    if (userInfo?.name?.isNotBlank() == true) {
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
                userInfoViewModel = userInfoViewModel, // pass viewmodel to save user info
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

        composable("questionnaire_verify") {
            QuestionnaireVerifyScreen(
                navController = navController,
                onVerified = {
                    navController.navigate("forgot_pin") {
                        popUpTo("questionnaire_verify") { inclusive = true }
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
        topBar = {
            ProfileTopBar(navController = navController)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        NavHostContainer(
            navController = navController,
            padding = padding
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "GI Health",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.Black
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {

                    DropdownMenuItem(
                        text = { Text("Edit Profile") },
                        onClick = {
                            menuExpanded = false
                            navController.navigate("profile")
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            menuExpanded = false
                            navController.navigate("settings")
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}

@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    val context = LocalContext.current

    val vm: CalendarViewModel = viewModel()
    val symptomViewModel: SymptomViewModel = viewModel()
    val foodVm: FoodViewModel = viewModel()

    // today's foods, for the Food screen
    val todayFoods by foodVm.todayFoods.observeAsState(emptyList())

    // shared JournalViewModel (same as in JournalScreen)
    val journalViewModel: JournalViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            context.applicationContext as Application
        )
    )

    // ALL foods for calendar (we load them directly from DB)
    var allFoods by remember { mutableStateOf<List<FoodEntity>>(emptyList()) }

    // Any time today's list changes (user logs something), re-query all foods
    LaunchedEffect(todayFoods) {
        val db = FoodDatabase.getDatabase(context)
        allFoods = db.foodDao().getAllFoods()
    }

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
            JournalScreen(journalViewModel = journalViewModel)
        }

        composable("select_symptoms") {
            SelectSymptomsScreen(navController = navController)
        }

        composable("rate_symptoms") {
            RateSymptomsScreen(navController = navController)
        }

        composable("logWeight") {
            LogWeightScreen(navController)
        }

        composable("analytics") {
            AnalyticsScreen(
                onOpenCalendar = { navController.navigate("calendar") },
                vm = vm,
                onGeneratePdf = {
                    navController.navigate("pdf_questionnaire")
                }
            )
        }

        composable("pdf_questionnaire") {
            val symptoms by symptomViewModel.symptoms.collectAsState()

            UserPDFQuestionnaire(
                symptoms = symptoms,
                navController = navController
            )
        }

        composable("calendar") {

            // Food dates (from LogFoodScreen) use "MMM d, yyyy" like "Feb 27, 2025"
            val foodDateFormatter = remember {
                DateTimeFormatter.ofPattern("MMM d, yyyy")
            }

            // Journal dates use "MMMM d, yyyy" like "February 27, 2025"
            val journalDateFormatter = remember {
                DateTimeFormatter.ofPattern("MMMM d, yyyy")
            }

            val journalEntries by journalViewModel.journalEntries.collectAsState()
            val symptoms by symptomViewModel.symptoms.collectAsState(initial = emptyList())

            FullCalendarScreen(
                onClose = { navController.popBackStack() },
                vm = vm,

                // FOOD for selected date
                getFoodForDate = { date ->
                    val dateString = date.format(foodDateFormatter)

                    val foodsForDate = allFoods.filter { it.date == dateString }

                    if (foodsForDate.isEmpty()) null
                    else FoodDay(
                        breakfast = foodsForDate
                            .filter { it.meal == "Breakfast" }
                            .joinToString { it.name },
                        lunch = foodsForDate
                            .filter { it.meal == "Lunch" }
                            .joinToString { it.name },
                        dinner = foodsForDate
                            .filter { it.meal == "Dinner" }
                            .joinToString { it.name }
                    )
                },

                // JOURNAL for selected date
                getJournalForDate = { date ->
                    val dateString = date.format(journalDateFormatter)

                    journalEntries
                        .filter { it.date == dateString }
                        .joinToString("\n\n") { it.entry }
                        .ifBlank { null }
                },

                // SYMPTOMS stays the same
                getSymptomsForDate = { date ->
                    val startOfDay = date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    val endOfDay = date
                        .plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    val daySymptoms = symptoms.filter { it.timestamp in startOfDay until endOfDay }

                    if (daySymptoms.isEmpty()) null
                    else daySymptoms.joinToString("\n") {
                        "${it.name}: ${it.severity}/10"
                    }
                }
            )
        }

        composable("profile") {
            ProfileScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController)
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
