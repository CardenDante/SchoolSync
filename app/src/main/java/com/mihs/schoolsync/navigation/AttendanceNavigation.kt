// AttendanceNavigation.kt
package com.mihs.schoolsync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mihs.schoolsync.ui.screens.*
import org.threeten.bp.LocalDate

// Navigation routes
object AttendanceRoutes {
    const val DAILY = "attendance/daily"
    const val COURSE = "attendance/course/{courseOfferingId}"
    const val CAMBRIDGE = "attendance/cambridge/{cambridgeSubjectId}"
    const val QR_CHECK_IN = "attendance/qr-check-in"
    const val MANUAL_CHECK_IN = "attendance/manual-check-in"
    const val TODAYS_CHECK_INS = "attendance/todays-check-ins"
    const val DASHBOARD = "attendance/dashboard"
    const val REPORTS = "attendance/reports"
    const val STUDENT_ATTENDANCE = "attendance/student/{studentId}"
    const val CLASS_ATTENDANCE = "attendance/class/{classSectionId}"

    // Helper functions to generate routes with parameters
    fun courseAttendance(courseOfferingId: Int) =
        "attendance/course/$courseOfferingId"

    fun cambridgeAttendance(cambridgeSubjectId: Int) =
        "attendance/cambridge/$cambridgeSubjectId"

    fun studentAttendance(studentId: Int) =
        "attendance/student/$studentId"

    fun classAttendance(classSectionId: Int) =
        "attendance/class/$classSectionId"
}

// Add attendance routes to the NavGraphBuilder
fun NavGraphBuilder.attendanceNavGraph(navController: NavHostController) {
    composable(AttendanceRoutes.DAILY) {
        DailyAttendanceScreen(
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = AttendanceRoutes.COURSE,
        arguments = listOf(
            navArgument("courseOfferingId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val courseOfferingId = backStackEntry.arguments?.getInt("courseOfferingId") ?: 0
        CourseAttendanceScreen(
            courseOfferingId = courseOfferingId,
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = AttendanceRoutes.CAMBRIDGE,
        arguments = listOf(
            navArgument("cambridgeSubjectId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val cambridgeSubjectId = backStackEntry.arguments?.getInt("cambridgeSubjectId") ?: 0
        CambridgeSubjectAttendanceScreen(
            cambridgeSubjectId = cambridgeSubjectId,
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(AttendanceRoutes.QR_CHECK_IN) {
        QRCheckInScreen(
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(AttendanceRoutes.MANUAL_CHECK_IN) {
        ManualCheckInScreen(
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(AttendanceRoutes.TODAYS_CHECK_INS) {
        TodaysCheckInsScreen(
            navigateBack = { navController.popBackStack() },
            navigateToCheckIn = { navController.navigate(AttendanceRoutes.MANUAL_CHECK_IN) },
            navigateToQrCheckIn = { navController.navigate(AttendanceRoutes.QR_CHECK_IN) }
        )
    }

    composable(AttendanceRoutes.DASHBOARD) {
        AttendanceDashboardScreen(
            navigateToClassAttendance = { classSectionId ->
                navController.navigate(AttendanceRoutes.classAttendance(classSectionId))
            },
            navigateToReports = {
                navController.navigate(AttendanceRoutes.REPORTS)
            }
        )
    }

    composable(AttendanceRoutes.REPORTS) {
        AttendanceReportsScreen(
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = AttendanceRoutes.STUDENT_ATTENDANCE,
        arguments = listOf(
            navArgument("studentId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
        StudentAttendanceScreen(
            studentId = studentId,
            navigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = AttendanceRoutes.CLASS_ATTENDANCE,
        arguments = listOf(
            navArgument("classSectionId") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val classSectionId = backStackEntry.arguments?.getInt("classSectionId") ?: 0
        ClassAttendanceScreen(
            classSectionId = classSectionId,
            navigateBack = { navController.popBackStack() },
            navigateToDaily = { classId, date ->
                // Handle navigate to daily attendance with class ID and date
                navController.navigate(AttendanceRoutes.DAILY)
            },
            navigateToCourse = { classId, courseId, date ->
                // Handle navigate to course attendance
                navController.navigate(AttendanceRoutes.courseAttendance(courseId))
            }
        )
    }
}