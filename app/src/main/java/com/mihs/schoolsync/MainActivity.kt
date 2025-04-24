package com.mihs.schoolsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mihs.schoolsync.navigation.AuthNavigation
import com.mihs.schoolsync.navigation.MainNavigation
import com.mihs.schoolsync.ui.theme.SchoolSyncTheme
import com.mihs.schoolsync.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Use this instead:
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolSyncTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isAuthenticated by remember { mutableStateOf(false) }
                    val navController = rememberNavController()

                    // Check authentication status
                    LaunchedEffect(key1 = Unit) {
                        isAuthenticated = authViewModel.isUserLoggedIn()
                    }

                    if (isAuthenticated) {
                        MainNavigation(
                            navController = navController,
                            onLogout = {
                                authViewModel.logout()
                                isAuthenticated = false
                            }
                        )
                    } else {
                        AuthNavigation(
                            onAuthSuccess = {
                                isAuthenticated = true
                            }
                        )
                    }
                }
            }
        }
    }
}