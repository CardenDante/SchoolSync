package com.mihs.schoolsync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mihs.schoolsync.ui.screens.student.*
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel

sealed class StudentScreens(val route: String) {
    object List : StudentScreens("students")
    object Detail : StudentScreens("students/{studentId}") {
        fun createRoute(studentId: Int) = "students/$studentId"
    }
    object Edit : StudentScreens("students/{studentId}/edit") {
        fun createRoute(studentId: Int) = "students/$studentId/edit"
    }
    object StatusUpdate : StudentScreens("students/{studentId}/status") {
        fun createRoute(studentId: Int) = "students/$studentId/status"
    }
    object Add : StudentScreens("students/add")
    object Filter : StudentScreens("students/filter")
    object ClassSection : StudentScreens("class-sections/{classSectionId}/students") {
        fun createRoute(classSectionId: Int) = "class-sections/$classSectionId/students"
    }
}

@Composable
fun StudentNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = StudentScreens.List.route,
    onNavigateBack: () -> Unit
) {
    val studentViewModel: StudentViewModel = viewModel()

    // Remember the filters state
    val filterState = remember {
        mutableStateOf(StudentFilters())
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Student List Screen
        composable(StudentScreens.List.route) {
            StudentListScreen(
                viewModel = studentViewModel,
                onStudentClick = { studentId ->
                    navController.navigate(StudentScreens.Detail.createRoute(studentId))
                },
                onAddStudentClick = {
                    navController.navigate(StudentScreens.Add.route)
                },
                onFilterClick = {
                    navController.navigate(StudentScreens.Filter.route)
                },
                onNavigateBack = onNavigateBack
            )
        }

        // Student Detail Screen
        composable(
            route = StudentScreens.Detail.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentDetailScreen(
                studentId = studentId,
                viewModel = studentViewModel,
                onEditClick = {
                    navController.navigate(StudentScreens.Edit.createRoute(studentId))
                },
                onUpdateStatusClick = {
                    navController.navigate(StudentScreens.StatusUpdate.createRoute(studentId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Student Edit Screen
        composable(
            route = StudentScreens.Edit.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentEditScreen(
                studentId = studentId,
                viewModel = studentViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditSuccess = {
                    // Pop back to detail screen and refresh
                    navController.popBackStack()
                    studentViewModel.getStudent(studentId)
                }
            )
        }

        // Student Status Update Screen
        composable(
            route = StudentScreens.StatusUpdate.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentStatusUpdateScreen(
                studentId = studentId,
                viewModel = studentViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUpdateSuccess = {
                    // Pop back to detail screen and refresh
                    navController.popBackStack()
                    studentViewModel.getStudent(studentId)
                }
            )
        }

        // Add Student Screen
        composable(StudentScreens.Add.route) {
            AddStudentScreen(
                viewModel = studentViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStudentAdded = { studentId ->
                    // Navigate to the newly created student's detail screen
                    navController.popBackStack()
                    navController.navigate(StudentScreens.Detail.createRoute(studentId))
                }
            )
        }

        // Student Filter Screen
        composable(StudentScreens.Filter.route) {
            StudentFilterScreen(
                initialFilters = filterState.value,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onApplyFilters = { newFilters ->
                    // Update our remembered filters
                    filterState.value = newFilters

                    // Apply the filters and navigate back
                    navController.popBackStack()

                    // Apply filters to view model
                    studentViewModel.getStudents(
                        studentId = newFilters.studentId,
                        status = newFilters.status?.toString(),
                        isActive = newFilters.isActive,
                        admissionDateStart = newFilters.admissionDateStart,
                        admissionDateEnd = newFilters.admissionDateEnd,
                        classSectionId = newFilters.classSectionId
                    )
                },
                onClearFilters = {
                    // Clear our remembered filters
                    filterState.value = StudentFilters()

                    // Apply the cleared filters and navigate back
                    navController.popBackStack()

                    // Reset to unfiltered list
                    studentViewModel.getStudents()
                }
            )
        }

        // Students by Class Section Screen
        composable(
            route = StudentScreens.ClassSection.route,
            arguments = listOf(
                navArgument("classSectionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val classSectionId = backStackEntry.arguments?.getInt("classSectionId") ?: 0

            // Load students by class section when the screen is composed
            LaunchedEffect(classSectionId) {
                studentViewModel.getStudentsByClassSection(classSectionId)
            }

            StudentListScreen(
                viewModel = studentViewModel,
                onStudentClick = { studentId ->
                    navController.navigate(StudentScreens.Detail.createRoute(studentId))
                },
                onAddStudentClick = {
                    navController.navigate(StudentScreens.Add.route)
                },
                onFilterClick = {
                    // Filtering not available in class section view
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}