package com.example.gihealth.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalyticsScreen(
    onOpenCalendar: () -> Unit,
    vm: CalendarViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("Analytics", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Last 7 Days Overview", fontSize = 16.sp, color = Color.DarkGray)
        }
        item { DigestiveComfortCard() }
        item { MoodCalendarWidget(vm = vm, onOpen = onOpenCalendar) }
        item { RecentTrendsCard() }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun DigestiveComfortCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Digestive Comfort (1–10)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentAlignment = Alignment.Center
            ) { Text("Graph Placeholder") }
        }
    }
}

@Composable
fun RecentTrendsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Recent Trends:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("• Digestive comfort improved after reducing dairy intake.")
            Text("• Symptoms were mild on days with increased hydration.")
            Text("• Slight discomfort noted after eating spicy foods.")
            Text("• Overall improvement compared to previous week.")
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("4 Improved", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                Text(" | 0 Stable | ")
                Text("3 Worse", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
        }
    }
}
