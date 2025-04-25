package com.mihs.schoolsync.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import com.mihs.schoolsync.navigation.AttendanceRoutes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mihs.schoolsync.navigation.NavigationRoutes

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("Dashboard", Icons.Default.Dashboard, NavigationRoutes.Dashboard.route),
        BottomNavItem("Students", Icons.Default.People, NavigationRoutes.Students.route),
        // Connect directly to attendance dashboard instead of the placeholder route
        BottomNavItem("Attendance", Icons.Default.CheckCircle, AttendanceRoutes.DASHBOARD),
        BottomNavItem("Courses", Icons.Default.School, NavigationRoutes.Courses.route),
        BottomNavItem("Finance", Icons.Default.AccountBalance, NavigationRoutes.Finances.route)
    )

    // Get current route to highlight the selected item
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            // Check if this nav item matches the current route
            val selected = when (item.route) {
                AttendanceRoutes.DASHBOARD -> {
                    // Consider selected for any attendance route
                    currentRoute == item.route ||
                            (currentRoute?.startsWith("attendance/") == true)
                }
                else -> {
                    // Standard route matching for other items
                    currentRoute == item.route ||
                            (currentRoute?.startsWith(item.route.substringBefore("/")) == true)
                }
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    // Only navigate if we're not already on this route
                    if (!selected) {
                        navController.navigate(item.route) {
                            // Pop up to the dashboard but not inclusive - keeps dashboard on the stack
                            popUpTo(NavigationRoutes.Dashboard.route) {
                                inclusive = false
                            }
                            // Avoid multiple copies of the same destination on the back stack
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}