package com.example.gihealth.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.gihealth.models.BottomNavItem


object Constants {
    val BottomNavItems = listOf(
        // Food screen
        BottomNavItem(
            label = "Food",
            icon = Icons.Filled.Dining,
            route = "food"
        ),
        // Symptoms screen
        BottomNavItem(
            label = "Symptom",
            icon = Icons.Filled.Sick,
            route = "symptoms"
        ),
        // Add new food/ symptom screen
        BottomNavItem(
            label = "Add",
            icon = Icons.Filled.AddCircle,
            route = "add"
        ),
        // Journal screen
        BottomNavItem(
            label = "Journal",
            icon = Icons.Filled.AutoStories,
            route = "journal"
        ),
        // Analytics screen
        BottomNavItem(
            label = "Analytics",
            icon = Icons.Filled.BarChart,
            route = "analytics"
        )

    )
}