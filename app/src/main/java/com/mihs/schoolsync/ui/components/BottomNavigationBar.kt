// BottomNavigationBar.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mihs.schoolsync.navigation.NavigationRoutes
import com.mihs.schoolsync.ui.finance.navigation.FINANCE_ROUTE

/**
 * Bottom navigation bar for the app
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    // Update to use BottomNavWithFinance if needed
    // or keep this implementation

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        // Dashboard Item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentDestination?.hierarchy?.any { it.route == NavigationRoutes.Dashboard.route } == true,
            onClick = {
                if (currentDestination?.route != NavigationRoutes.Dashboard.route) {
                    navController.navigate(NavigationRoutes.Dashboard.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        // Students Item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Group, contentDescription = "Students") },
            label = { Text("Students") },
            selected = currentDestination?.hierarchy?.any { it.route == NavigationRoutes.StudentList.route } == true,
            onClick = {
                if (currentDestination?.route != NavigationRoutes.StudentList.route) {
                    navController.navigate(NavigationRoutes.StudentList.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        // Attendance Item
        NavigationBarItem(
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Attendance") },
            label = { Text("Attendance") },
            selected = currentDestination?.hierarchy?.any { it.route == NavigationRoutes.Attendance.route } == true,
            onClick = {
                if (currentDestination?.route != NavigationRoutes.Attendance.route) {
                    navController.navigate(NavigationRoutes.Attendance.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        // Finance Item
        NavigationBarItem(
            icon = { Icon(Icons.Default.AttachMoney, contentDescription = "Finance") },
            label = { Text("Finance") },
            selected = currentDestination?.hierarchy?.any { it.route?.startsWith(FINANCE_ROUTE) == true } == true,
            onClick = {
                if (currentDestination?.route != FINANCE_ROUTE) {
                    navController.navigate(FINANCE_ROUTE) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )

        // Profile Item
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentDestination?.hierarchy?.any { it.route == NavigationRoutes.UserProfile.route } == true,
            onClick = {
                if (currentDestination?.route != NavigationRoutes.UserProfile.route) {
                    navController.navigate(NavigationRoutes.UserProfile.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}