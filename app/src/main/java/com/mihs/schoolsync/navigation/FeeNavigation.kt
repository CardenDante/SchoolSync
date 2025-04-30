//// FeeNavigation.kt
//package com.mihs.schoolsync.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavType
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.mihs.schoolsync.ui.screens.fee.*
//
//const val FEE_ROUTE = "fee"
//const val FEE_STATEMENT_ROUTE = "fee_statement"
//const val FEE_STRUCTURE_ROUTE = "fee_structure"
//const val FEE_STRUCTURE_CREATE_ROUTE = "fee_structure_create"
//const val FEE_STRUCTURE_ASSIGN_ROUTE = "fee_structure_assign"
//const val PAYMENT_SUBMIT_ROUTE = "payment_submit"
//const val PAYMENTS_PENDING_ROUTE = "payments_pending"
//const val PAYMENT_VERIFY_ROUTE = "payment_verify"
//const val PAYMENT_RECEIPT_ROUTE = "payment_receipt"
//const val PAYMENT_STUDENT_HISTORY_ROUTE = "payment_student_history"
//const val PAYMENT_REQUEST_ROUTE = "payment_request"
//
//fun NavGraphBuilder.feeNavigation(navController: NavController) {
//
//    // Fee Statement Screen - Show student's fee account and transactions
//    composable(
//        route = "$FEE_STATEMENT_ROUTE/{studentId}",
//        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
//        FeeStatementScreen(
//            studentId = studentId,
//            navigateBack = { navController.popBackStack() },
//            navigateToSubmitPayment = { navController.navigate("$PAYMENT_SUBMIT_ROUTE/$studentId") },
//            navigateToPaymentHistory = { navController.navigate("$PAYMENT_STUDENT_HISTORY_ROUTE/$studentId") }
//        )
//    }
//
//    // Fee Structures List Screen - Show all fee structures
//    composable(route = FEE_STRUCTURE_ROUTE) {
//        FeeStructuresScreen(
//            navigateBack = { navController.popBackStack() },
//            navigateToCreateFeeStructure = { navController.navigate(FEE_STRUCTURE_CREATE_ROUTE) },
//            navigateToAssignFeeStructure = { classLevelId ->
//                navController.navigate("$FEE_STRUCTURE_ASSIGN_ROUTE/$classLevelId")
//            },
//            navigateToFeeStructureDetail = { structureId ->
//                navController.navigate("$FEE_STRUCTURE_ROUTE/$structureId")
//            }
//        )
//    }
//
//    // Fee Structure Detail Screen - Show a specific fee structure
//    composable(
//        route = "$FEE_STRUCTURE_ROUTE/{structureId}",
//        arguments = listOf(navArgument("structureId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val structureId = backStackEntry.arguments?.getInt("structureId") ?: 0
//        FeeStructureDetailScreen(
//            structureId = structureId,
//            navigateBack = { navController.popBackStack() }
//        )
//    }
//
//    // Create Fee Structure Screen - Create a new fee structure with items
//    composable(route = FEE_STRUCTURE_CREATE_ROUTE) {
//        CreateFeeStructureScreen(
//            navigateBack = { navController.popBackStack() },
//            onFeeStructureCreated = { navController.popBackStack() }
//        )
//    }
//
//    // Assign Fee Structure Screen - Assign fee structure to students
//    composable(
//        route = "$FEE_STRUCTURE_ASSIGN_ROUTE/{classLevelId}",
//        arguments = listOf(navArgument("classLevelId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val classLevelId = backStackEntry.arguments?.getInt("classLevelId") ?: 0
//        AssignFeeStructureScreen(
//            classLevelId = classLevelId,
//            navigateBack = { navController.popBackStack() },
//            onFeeStructureAssigned = { navController.popBackStack() }
//        )
//    }
//
//    // Submit Payment Screen - For parents to submit payment details
//    composable(
//        route = "$PAYMENT_SUBMIT_ROUTE/{studentId}",
//        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
//        SubmitPaymentScreen(
//            studentId = studentId,
//            navigateBack = { navController.popBackStack() },
//            onPaymentSubmitted = { navController.popBackStack() }
//        )
//    }
//
//    // Pending Payments Screen - List of payments awaiting verification
//    composable(route = PAYMENTS_PENDING_ROUTE) {
//        PendingPaymentsScreen(
//            navigateBack = { navController.popBackStack() },
//            navigateToVerifyPayment = { paymentId ->
//                navController.navigate("$PAYMENT_VERIFY_ROUTE/$paymentId")
//            }
//        )
//    }
//
//    // Verify Payment Screen - Admin verifies a payment
//    composable(
//        route = "$PAYMENT_VERIFY_ROUTE/{paymentId}",
//        arguments = listOf(navArgument("paymentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0
//        VerifyPaymentScreen(
//            paymentId = paymentId,
//            navigateBack = { navController.popBackStack() },
//            navigateToGenerateReceipt = { verifiedPaymentId ->
//                navController.navigate("$PAYMENT_RECEIPT_ROUTE/$verifiedPaymentId")
//            },
//            onPaymentVerified = { navController.popBackStack() }
//        )
//    }
//
//    // Generate Receipt Screen - Create a receipt for a verified payment
//    composable(
//        route = "$PAYMENT_RECEIPT_ROUTE/{paymentId}",
//        arguments = listOf(navArgument("paymentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val paymentId = backStackEntry.arguments?.getInt("paymentId") ?: 0
//        ReceiptScreen(
//            paymentId = paymentId,
//            navigateBack = { navController.popBackStack() }
//        )
//    }
//
//    // Student Payment History Screen - List of all payments for a student
//    composable(
//        route = "$PAYMENT_STUDENT_HISTORY_ROUTE/{studentId}",
//        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
//        StudentPaymentHistoryScreen(
//            studentId = studentId,
//            navigateBack = { navController.popBackStack() },
//            navigateToReceipt = { paymentId ->
//                navController.navigate("$PAYMENT_RECEIPT_ROUTE/$paymentId")
//            }
//        )
//    }
//
//    // Create Payment Request Screen - Admin creates a payment request
//    composable(
//        route = "$PAYMENT_REQUEST_ROUTE/{studentId}",
//        arguments = listOf(navArgument("studentId") { type = NavType.IntType })
//    ) { backStackEntry ->
//        val studentId = backStackEntry.arguments?.getInt("studentId") ?: 0
//        CreatePaymentRequestScreen(
//            studentId = studentId,
//            navigateBack = { navController.popBackStack() },
//            onRequestCreated = { navController.popBackStack() }
//        )
//    }
//}