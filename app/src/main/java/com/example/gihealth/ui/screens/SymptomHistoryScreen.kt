package com.example.gihealth.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gihealth.data.*
import java.net.URLEncoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow


class SymptomHistoryViewModel(
    private val dao: SymptomDao
) : ViewModel() {

    // All symptom names for search / selection
    val symptoms: Flow<List<String>> = dao.getAllSymptomNames()

    var selectedSymptom by mutableStateOf<String?>(null)
        private set

    var lastLoggedDate by mutableStateOf<String?>(null)
        private set

    fun selectSymptom(symptom: String) {
        selectedSymptom = symptom
        // Launch a coroutine to get last logged date
        viewModelScope.launch {
            lastLoggedDate = dao.getLastLoggedDate(symptom)
        }
    }
}

class SymptomHistoryViewModelFactory(
    private val dao: SymptomDao
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SymptomHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SymptomHistoryViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun SymptomHistoryScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val dao = SymptomDatabase.getDatabase(context).symptomDao() // your DB & DAO

    val vm: SymptomHistoryViewModel = viewModel(
        factory = SymptomHistoryViewModelFactory(dao)
    )

    val symptoms by vm.symptoms.collectAsState(initial = emptyList())

    var search by remember { mutableStateOf("") }

    val filtered = symptoms.filter { symptom ->
        symptom.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Search Symptom History",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            label = { Text("Search symptoms") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(filtered) { symptom ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.selectSymptom(symptom) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = vm.selectedSymptom == symptom,
                        onClick = { vm.selectSymptom(symptom) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF0F9D58)
                        )
                    )

                    Spacer(Modifier.width(8.dp))
                    Text(symptom)
                }
            }
        }

        vm.lastLoggedDate?.let { date ->

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Last Logged: $date",
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(8.dp))
            val encodedDate = URLEncoder.encode(date, "UTF-8")

            Button(
                onClick = {
                    navController.navigate("calendar?date=$encodedDate")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F9D58),
                    contentColor = Color.White
                )
            ) {
                Text("Go to That Day")
            }
        }
    }
}