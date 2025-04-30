// FinanceReportsScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.finance.viewmodel.FinanceReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceReportsScreen(
    navigateBack: () -> Unit,
    onViewPaymentsByDateRange: (Date, Date) -> Unit,
    onViewFeeStructureAssignments: () -> Unit,
    onViewOutstandingBalances: () -> Unit,
    financeReportViewModel: FinanceReportViewModel = hiltViewModel()
) {
    // Get finance summary
    LaunchedEffect(Unit) {
        financeReportViewModel.getFinanceSummary()
    }

    // Observe state
    val summaryState by financeReportViewModel.summaryState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Reports") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (summaryState) {
                is FinanceReportViewModel.SummaryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FinanceReportViewModel.SummaryState.Error -> {
                    val errorMessage = (summaryState as FinanceReportViewModel.SummaryState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { financeReportViewModel.getFinanceSummary() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is FinanceReportViewModel.SummaryState.Success -> {
                    val summary = (summaryState as FinanceReportViewModel.SummaryState.Success).summary

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Financial Summary Section
                        item {
                            Text(
                                text = "Financial Summary",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            SummaryCard(
                                title = "Total Outstanding Fees",
                                amount = summary.totalOutstandingFees,
                                color = MaterialTheme.colorScheme.error,
                                icon = Icons.Default.Money
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            SummaryCard(
                                title = "Payments Collected (This Month)",
                                amount = summary.paymentsThisMonth,
                                color = MaterialTheme.colorScheme.primary,
                                icon = Icons.Default.Payments
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            SummaryCard(
                                title = "Payments Collected (This Year)",
                                amount = summary.paymentsThisYear,
                                color = MaterialTheme.colorScheme.primary,
                                icon = Icons.Default.Payments
                            )
                        }

                        // Available Reports Section
                        item {
                            Text(
                                text = "Available Reports",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            ReportCard(
                                title = "Payments by Date Range",
                                description = "View payments received within a specific date range",
                                icon = Icons.Default.DateRange,
                                onClick = {
                                    // For now, use fixed date range as an example
                                    val endDate = Calendar.getInstance().time
                                    val startDate = Calendar.getInstance().apply {
                                        add(Calendar.MONTH, -1) // Last month
                                    }.time

                                    onViewPaymentsByDateRange(startDate, endDate)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ReportCard(
                                title = "Fee Structure Assignments",
                                description = "View which fee structures are assigned to students",
                                icon = Icons.Default.AssignmentInd,
                                onClick = onViewFeeStructureAssignments
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            ReportCard(
                                title = "Outstanding Balances",
                                description = "View students with outstanding fee balances",
                                icon = Icons.Default.AccountBalance,
                                onClick = onViewOutstandingBalances
                            )
                        }

                        // Recent Activity Section
                        item {
                            Text(
                                text = "Recent Activity",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (summary.recentTransactions.isEmpty()) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No recent transactions found",
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    summary.recentTransactions.forEach { transaction ->
                                        RecentTransactionItem(transaction)
                                    }
                                }
                            }
                        }

                        // Add bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }

                else -> {
                    // Idle state, do nothing
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = color
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatMoney(amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "View Report"
            )
        }
    }
}

@Composable
fun RecentTransactionItem(transaction: FinanceReportViewModel.TransactionSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on transaction type
            val icon = when (transaction.type) {
                "PAYMENT" -> Icons.Default.Payment
                "CHARGE" -> Icons.Default.Add
                "WAIVER" -> Icons.Default.MoneyOff
                else -> Icons.Default.SwapHoriz
            }

            val iconColor = when (transaction.type) {
                "PAYMENT" -> Color.Green
                "CHARGE" -> MaterialTheme.colorScheme.error
                "WAIVER" -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.primary
            }

            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.2f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = transaction.type,
                        tint = iconColor
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Student: ${transaction.studentName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(transaction.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            val amountPrefix = if (transaction.type == "PAYMENT" || transaction.type == "WAIVER") "-" else "+"
            Text(
                text = "$amountPrefix${formatMoney(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == "PAYMENT" || transaction.type == "WAIVER") Color.Green else MaterialTheme.colorScheme.error
            )
        }
    }
}