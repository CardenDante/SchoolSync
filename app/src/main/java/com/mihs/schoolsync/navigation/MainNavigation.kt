package com.mihs.schoolsync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mihs.schoolsync.ui.screens.*
import com.mihs.schoolsync.ui.screens.user.*
import com.mihs.schoolsync.ui.screens.student.*

@Composable
fun MainNavigation(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.Dashboard.route
    ) {
        composable(NavigationRoutes.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                onAttendanceClick = {
                    navController.navigate(NavigationRoutes.Attendance.route)
                },
                onStudentsClick = {
                    // Navigate to student list screen directly
                    navController.navigate(NavigationRoutes.StudentList.route)
                },
                onCoursesClick = {
                    navController.navigate(NavigationRoutes.Courses.route)
                },
                onFinancesClick = {
                    navController.navigate(NavigationRoutes.Finances.route)
                },
                onProfileClick = {
                    navController.navigate(NavigationRoutes.UserProfile.route)
                },
                onLogout = onLogout
            )
        }

        // User Management Routes
        composable(NavigationRoutes.UserProfile.route) {
            UserProfileScreen(
                onEditProfile = {
                    navController.navigate(NavigationRoutes.EditProfile.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.EditProfile.route) {
            EditProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.UserList.route) {
            UserListScreen(
                onUserClick = { userId ->
                    navController.navigate("${NavigationRoutes.UserDetail.route}/$userId")
                },
                onAddUserClick = {
                    navController.navigate(NavigationRoutes.CreateUser.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${NavigationRoutes.UserDetail.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserDetailScreen(
                userId = userId,
                onEditClick = { editUserId ->
                    navController.navigate("${NavigationRoutes.EditUser.route}/$editUserId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.CreateUser.route) {
            CreateUserScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${NavigationRoutes.EditUser.route}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            EditUserScreen(
                userId = userId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Placeholder module screens
        composable(NavigationRoutes.Attendance.route) {
            AttendanceScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // STUDENT MANAGEMENT ROUTES
        // Replace the placeholder StudentsScreen with our actual student screens

        // Main Students Landing Page
        composable(NavigationRoutes.Students.route) {
            // Integration point - this is where we'll show the student management landing page
            StudentListScreen(
                onStudentClick = { studentId ->
                    navController.navigate(NavigationRoutes.StudentDetail.createRoute(studentId))
                },
                onAddStudentClick = {
                    navController.navigate(NavigationRoutes.StudentAdd.route)
                },
                onFilterClick = {
                    navController.navigate(NavigationRoutes.StudentFilter.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Student List Screen (same as Students route, but listed explicitly)
        composable(NavigationRoutes.StudentList.route) {
            StudentListScreen(
                onStudentClick = { studentId ->
                    navController.navigate(NavigationRoutes.StudentDetail.createRoute(studentId))
                },
                onAddStudentClick = {
                    navController.navigate(NavigationRoutes.StudentAdd.route)
                },
                onFilterClick = {
                    navController.navigate(NavigationRoutes.StudentFilter.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Student Detail Screen
        composable(
            route = NavigationRoutes.StudentDetail.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentDetailScreen(
                studentId = studentId,
                onEditClick = {
                    navController.navigate(NavigationRoutes.StudentEdit.createRoute(studentId))
                },
                onUpdateStatusClick = {
                    navController.navigate(NavigationRoutes.StudentStatus.createRoute(studentId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Student Edit Screen
        composable(
            route = NavigationRoutes.StudentEdit.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentEditScreen(
                studentId = studentId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Student Status Update Screen
        composable(
            route = NavigationRoutes.StudentStatus.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0

            StudentStatusUpdateScreen(
                studentId = studentId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUpdateSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Add Student Screen
        composable(NavigationRoutes.StudentAdd.route) {
            AddStudentScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onStudentAdded = { studentId ->
                    navController.popBackStack()
                    navController.navigate(NavigationRoutes.StudentDetail.createRoute(studentId))
                }
            )
        }

        // Student Filter Screen
        composable(NavigationRoutes.StudentFilter.route) {
            StudentFilterScreen(
                initialFilters = StudentFilters(),
                onNavigateBack = {
                    navController.popBackStack()
                },
                onApplyFilters = { _ ->
                    navController.popBackStack()
                },
                onClearFilters = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationRoutes.Courses.route) {
            CoursesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.Finances.route) {
            FinancesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// Update NavigationRoutes to include new student screens
sealed class NavigationRoutes(val route: String) {
    object Login : NavigationRoutes("login")
    object Register : NavigationRoutes("register")
    object Dashboard : NavigationRoutes("dashboard")
    object Attendance : NavigationRoutes("attendance")
    object Students : NavigationRoutes("students")
    object Courses : NavigationRoutes("courses")
    object Finances : NavigationRoutes("finances")

    // User Management Routes
    object UserProfile : NavigationRoutes("user_profile")
    object EditProfile : NavigationRoutes("edit_profile")
    object UserList : NavigationRoutes("user_list")
    object UserDetail : NavigationRoutes("user_detail")
    object CreateUser : NavigationRoutes("create_user")
    object EditUser : NavigationRoutes("edit_user")

    // Student Management Routes
    object StudentList : NavigationRoutes("student_list")
    object StudentDetail : NavigationRoutes("student_detail/{studentId}") {
        fun createRoute(studentId: Int) = "student_detail/$studentId"
    }
    object StudentEdit : NavigationRoutes("student_edit/{studentId}") {
        fun createRoute(studentId: Int) = "student_edit/$studentId"
    }
    object StudentStatus : NavigationRoutes("student_status/{studentId}") {
        fun createRoute(studentId: Int) = "student_status/$studentId"
    }
    object StudentAdd : NavigationRoutes("student_add")
    object StudentFilter : NavigationRoutes("student_filter")
}