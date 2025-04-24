package com.mihs.schoolsync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mihs.schoolsync.ui.screens.LoginScreen
import com.mihs.schoolsync.ui.screens.RegisterScreen

@Composable
fun AuthNavigation(
    navController: NavHostController = rememberNavController(),
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Login.route
    ) {
        composable(NavigationRoutes.Login.route) {
            LoginScreen(
                onLoginSuccess = onAuthSuccess,
                onRegisterClick = {
                    navController.navigate(NavigationRoutes.Register.route)
                }
            )
        }
        composable(NavigationRoutes.Register.route) {
            RegisterScreen(
                onRegistrationSuccess = {
                    navController.navigate(NavigationRoutes.Login.route) {
                        popUpTo(NavigationRoutes.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}