package com.mihs.schoolsync.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.mihs.schoolsync.navigation.NavigationRoutes

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Dashboard, NavigationRoutes.Dashboard.route),
        BottomNavItem("Students", Icons.Default.People, NavigationRoutes.Students.route),
        BottomNavItem("Attendance", Icons.Default.CheckCircle, NavigationRoutes.Attendance.route),
        BottomNavItem("Courses", Icons.Default.School, NavigationRoutes.Courses.route),
        BottomNavItem("Finance", Icons.Default.AccountBalance, NavigationRoutes.Finances.route)
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = false, // Optional: track current route for highlighting
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(NavigationRoutes.Dashboard.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
