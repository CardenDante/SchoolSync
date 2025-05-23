// MainActivity.kt (updated with hover event crash fix)
package com.mihs.schoolsync

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.mihs.schoolsync.navigation.AuthNavigation
import com.mihs.schoolsync.navigation.MainNavigation
import com.mihs.schoolsync.ui.theme.SchoolSyncTheme
import com.mihs.schoolsync.ui.viewmodel.AuthViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Use this instead for activity-level ViewModels
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

                    // Get ViewModels for student management
                    val studentViewModel: StudentViewModel = hiltViewModel()

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
                            },
                            authViewModel = authViewModel,
                            studentViewModel = studentViewModel
                        )
                    } else {
                        AuthNavigation(
                            onAuthSuccess = {
                                isAuthenticated = true
                            },
                            navController = navController,
                            authViewModel = authViewModel
                        )
                    }
                }
            }
        }
    }

    /**
     * Override to catch and handle the hover event issue that causes crashes
     * on some devices, particularly Xiaomi phones with MIUI.
     */
    override fun dispatchGenericMotionEvent(ev: MotionEvent): Boolean {
        return try {
            super.dispatchGenericMotionEvent(ev)
        } catch (e: IllegalStateException) {
            if (e.message?.contains("ACTION_HOVER_EXIT") == true) {
                // Log the error but don't crash
                Log.e("MainActivity", "Caught hover exit error", e)
                true // Consume the event
            } else {
                // Re-throw other exceptions
                throw e
            }
        }
    }
}