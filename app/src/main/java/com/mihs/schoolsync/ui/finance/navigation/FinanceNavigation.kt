// UpdatedFinanceNavigation.kt
package com.mihs.schoolsync.ui.finance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mihs.schoolsync.ui.finance.screens.*
import com.mihs.schoolsync.ui.viewmodel.AuthViewModel
import java.util.*

// Finance module route constants
const val FINANCE_ROUTE = "finance"
const val FEE_STATEMENT_ROUTE = "fee_statement"
const val PAYMENT_SUBMIT_ROUTE = "payment_submit"
const val PAYMENT_HISTORY_ROUTE = "payment_history"
const val PAYMENT_RECEIPT_ROUTE = "payment_receipt"
const val PENDING_PAYMENTS_ROUTE = "pending_payments"
const val FEE_STRUCTURES_ROUTE = "fee_structures"
const val CREATE_FEE_STRUCTURE_ROUTE = "create_fee_structure"
const val EDIT_FEE_STRUCTURE_ROUTE = "edit_fee_structure"
const val FINANCE_REPORTS_ROUTE = "finance_reports"
const val PAYMENTS_BY_DATE_ROUTE = "payments_by_date"
const val OUTSTANDING_BALANCES_ROUTE = "outstanding_balances"

// Helper functions to create route paths with parameters
fun feeStatementRoute(studentId: Int) = "$FEE_STATEMENT_ROUTE/$studentId"
fun paymentSubmitRoute(studentId: Int) = "$PAYMENT_SUBMIT_ROUTE/$studentId"
fun paymentHistoryRoute(studentId: Int) = "$PAYMENT_HISTORY_ROUTE/$studentId"
fun paymentReceiptRoute(paymentId: Int) = "$PAYMENT_RECEIPT_ROUTE/$paymentId"
fun editFeeStructureRoute(structureId: Int) = "$EDIT_FEE_STRUCTURE_ROUTE/$structureId"
fun paymentsByDateRoute(startDate: Long, endDate: Long) = "$PAYMENTS_BY_DATE_ROUTE/$startDate/$endDate"

/**
 * Updated finance navigation graph with all new screens
 */
fun NavGraphBuilder.updatedFinanceNavigation(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Finance Dashboard
    composable(FINANCE_ROUTE) {
        FinanceDashboard(
            navController = navController,
            isAdmin = authViewModel.isAdmin() // Use isAdmin from your AuthViewModel
        )
    }

    // Fee Statement Screen
    composable(
        route = "$FEE_STATEMENT_ROUTE/{studentId}",
        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
        FeeStatementScreen(
            studentId = studentId,
            navigateBack = { navController.popBackStack() },
            navigateToPayment = { navController.navigate(paymentSubmitRoute(studentId)) }
        )
    }

    // Make Payment Screen
    composable(
        route = "$PAYMENT_SUBMIT_ROUTE/{studentId}",
        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
        MakePaymentScreen(
            studentId = studentId,
            navigateBack = { navController.popBackStack() },
            onSuccess = {
                // Navigate back to fee statement after successful payment
                navController.popBackStack()
            }
        )
    }

    // Payment History Screen
    composable(
        route = "$PAYMENT_HISTORY_ROUTE/{studentId}",
        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
    ) { backStackEntry ->
        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
        PaymentHistoryScreen(
            studentId = studentId,
            navigateBack = { navController.popBackStack() },
            onGenerateReceipt = { paymentId ->
                navController.navigate(paymentReceiptRoute(paymentId))
            }
        )
    }

    // Payment Receipt Screen
    composable(
        route = "$PAYMENT_RECEIPT_ROUTE/{paymentId}",
        arguments = listOf(navArgument("paymentId") { type = NavType.IntType })
    ) { backStackEntry ->
        val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0
        ReceiptScreen(
            paymentId = paymentId,
            navigateBack = { navController.popBackStack() }
        )
    }

    // Pending Payments Screen (Admin only)
    composable(PENDING_PAYMENTS_ROUTE) {
        PendingPaymentsScreen(
            navigateBack = { navController.popBackStack() },
            onVerifySuccess = {
                // Just refresh the screen
            }
        )
    }

    // Fee Structures Screen (Admin only)
    composable(FEE_STRUCTURES_ROUTE) {
        FeeStructureScreen(
            navigateBack = { navController.popBackStack() },
            onCreateNew = { navController.navigate(CREATE_FEE_STRUCTURE_ROUTE) },
            onEditStructure = { structureId ->
                navController.navigate(editFeeStructureRoute(structureId))
            }
        )
    }

    // Create Fee Structure Screen (Admin only)
    composable(CREATE_FEE_STRUCTURE_ROUTE) {
        CreateFeeStructureScreen(
            navigateBack = { navController.popBackStack() },
            onSuccess = { structureId ->
                // Navigate back to fee structures after successful creation
                navController.popBackStack()
            }
        )
    }

    // Edit Fee Structure Screen (Admin only)
    composable(
        route = "$EDIT_FEE_STRUCTURE_ROUTE/{structureId}",
        arguments = listOf(navArgument("structureId") { type = NavType.IntType })
    ) { backStackEntry ->
        val structureId = backStackEntry.arguments?.getInt("structureId") ?: 0
        // You would implement an EditFeeStructureScreen similar to CreateFeeStructureScreen
        // but pre-populated with the existing fee structure data
        CreateFeeStructureScreen( // For now, reuse the create screen
            navigateBack = { navController.popBackStack() },
            onSuccess = { _ ->
                // Navigate back to fee structures after successful update
                navController.popBackStack()
            }
        )
    }

    // Finance Reports Screen (Admin only)
    composable(FINANCE_REPORTS_ROUTE) {
        FinanceReportsScreen(
            navigateBack = { navController.popBackStack() },
            onViewPaymentsByDateRange = { startDate, endDate ->
                // Convert dates to longs for navigation
                navController.navigate(paymentsByDateRoute(startDate.time, endDate.time))
            },
            onViewFeeStructureAssignments = {
                // Navigate to fee structure assignments report
            },
            onViewOutstandingBalances = {
                navController.navigate(OUTSTANDING_BALANCES_ROUTE)
            }
        )
    }

    // Payments By Date Range Report (Admin only)
    composable(
        route = "$PAYMENTS_BY_DATE_ROUTE/{startDate}/{endDate}",
        arguments = listOf(
            navArgument("startDate") { type = NavType.LongType },
            navArgument("endDate") { type = NavType.LongType }
        )
    ) { backStackEntry ->
        val startDate = Date(backStackEntry.arguments?.getLong("startDate") ?: 0)
        val endDate = Date(backStackEntry.arguments?.getLong("endDate") ?: 0)

        // PaymentsByDateReportScreen would be implemented to show payments in the date range
        // For now, we can placeholder it using a lambda that navigates back
        navController.popBackStack()
    }

    // Outstanding Balances Report (Admin only)
    composable(OUTSTANDING_BALANCES_ROUTE) {
        // OutstandingBalancesScreen would be implemented to show students with balances
        // For now, we can placeholder it using a lambda that navigates back
        navController.popBackStack()
    }
}