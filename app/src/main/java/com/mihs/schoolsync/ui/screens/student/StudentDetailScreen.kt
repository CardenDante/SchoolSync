package com.mihs.schoolsync.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.*
import com.mihs.schoolsync.ui.components.LoadingIndicator
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    studentId: Int,
    viewModel: StudentViewModel = hiltViewModel(),
    onEditClick: () -> Unit,
    onUpdateStatusClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val studentDetailState by viewModel.studentDetailState.collectAsState()
    val academicSummaryState by viewModel.academicSummaryState.collectAsState()
    val financialSummaryState by viewModel.financialSummaryState.collectAsState()
    val documentsState by viewModel.documentsState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Academic", "Financial", "Documents")

    // Load student details when screen is displayed
    LaunchedEffect(studentId) {
        viewModel.getStudent(studentId)
        viewModel.getStudentAcademicSummary(studentId)
        viewModel.getStudentFinancialSummary(studentId)
        viewModel.getStudentDocuments(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onUpdateStatusClick) {
                        Icon(
                            imageVector = Icons.Default.Update,
                            contentDescription = "Update Status"
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Loading or Error State
            when (val state = studentDetailState) {
                is StudentViewModel.StudentDetailState.Loading -> {
                    LoadingIndicator()
                }
                is StudentViewModel.StudentDetailState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Error: ${state.message}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                is StudentViewModel.StudentDetailState.Success -> {
                    val student = state.student

                    // Student Header
                    StudentHeader(student)

                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    // Tab Content
                    when (selectedTabIndex) {
                        0 -> StudentOverviewTab(student)
                        1 -> StudentAcademicTab(academicSummaryState)
                        2 -> StudentFinancialTab(financialSummaryState)
                        3 -> StudentDocumentsTab(documentsState)
                    }
                }
                else -> {} // Idle state, do nothing
            }
        }
    }
}

@Composable
fun StudentHeader(student: StudentDetail) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Student Icon with Status Color
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(getStatusColor(student.status).copy(alpha = 0.1f))
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = getStatusColor(student.status),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = student.studentId,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Badge(
                containerColor = getStatusColor(student.status),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = student.status.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Badge(
                containerColor = if (student.isActive) Color.Green else Color.Red
            ) {
                Text(
                    text = if (student.isActive) "Active" else "Inactive",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        student.currentClass?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Class: $it",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StudentOverviewTab(student: StudentDetail) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Basic Information Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(
                    label = "Student ID",
                    value = student.studentId,
                    icon = Icons.Default.Badge
                )

                InfoRow(
                    label = "Application ID",
                    value = student.applicationId.toString(),
                    icon = Icons.Default.Description
                )

                InfoRow(
                    label = "Status",
                    value = student.status.toString(),
                    icon = Icons.Default.Info,
                    valueColor = getStatusColor(student.status)
                )

                InfoRow(
                    label = "Active",
                    value = if (student.isActive) "Yes" else "No",
                    icon = Icons.Default.RadioButtonChecked,
                    valueColor = if (student.isActive) Color.Green else Color.Red
                )
            }
        }

        // Dates Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Dates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(
                    label = "Admission Date",
                    value = formatDateString(student.admissionDate),
                    icon = Icons.Default.CalendarToday
                )

                InfoRow(
                    label = "Last Updated",
                    value = formatDateString(student.lastUpdated),
                    icon = Icons.Default.Update
                )

                InfoRow(
                    label = "Created At",
                    value = formatDateString(student.createdAt),
                    icon = Icons.Default.AccessTime
                )
            }
        }

        // Current Class Card
        if (student.currentClass != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Current Class",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(
                        label = "Class",
                        value = student.currentClass,
                        icon = Icons.Default.School
                    )

                    student.currentAcademicYear?.let {
                        InfoRow(
                            label = "Academic Year",
                            value = it,
                            icon = Icons.Default.DateRange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentAcademicTab(academicSummaryState: StudentViewModel.AcademicSummaryState) {
    when (academicSummaryState) {
        is StudentViewModel.AcademicSummaryState.Loading -> {
            LoadingIndicator()
        }
        is StudentViewModel.AcademicSummaryState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading academic data: ${academicSummaryState.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        is StudentViewModel.AcademicSummaryState.Success -> {
            val summary = academicSummaryState.summary
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Current Enrollment Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Current Enrollment",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (summary.currentEnrollment != null) {
                            InfoRow(
                                label = "Class",
                                value = "${summary.currentEnrollment.classLevelName} ${summary.currentEnrollment.classSectionName}",
                                icon = Icons.Default.School
                            )

                            InfoRow(
                                label = "Academic Year",
                                value = summary.currentEnrollment.academicYearName,
                                icon = Icons.Default.DateRange
                            )

                            InfoRow(
                                label = "Status",
                                value = summary.currentEnrollment.status,
                                icon = Icons.Default.Info
                            )

                            InfoRow(
                                label = "Enrollment Date",
                                value = summary.currentEnrollment.enrollmentDate,
                                icon = Icons.Default.CalendarToday
                            )
                        } else {
                            Text(
                                text = "No current enrollment found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Attendance Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Attendance",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val attendanceRate = summary.attendanceRate ?: 0.0
                        AttendanceBar(
                            percentage = attendanceRate.toFloat(),
                            label = "Overall Attendance: ${String.format("%.1f", attendanceRate)}%"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val termAttendanceRate = summary.termAttendanceRate ?: 0.0
                        AttendanceBar(
                            percentage = termAttendanceRate.toFloat(),
                            label = "Term Attendance: ${String.format("%.1f", termAttendanceRate)}%"
                        )
                    }
                }

                // Enrollment History Card
                if (summary.enrollmentHistory.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Enrollment History",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            EnrollmentHistoryList(enrollments = summary.enrollmentHistory)
                        }
                    }
                }
            }
        }
        else -> {} // Idle state, do nothing
    }
}

@Composable
fun EnrollmentHistoryList(enrollments: List<StudentEnrollmentInfo>) {
    Column {
        enrollments.forEach { enrollment ->
            EnrollmentHistoryItem(enrollment)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun EnrollmentHistoryItem(enrollment: StudentEnrollmentInfo) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${enrollment.classLevelName} ${enrollment.classSectionName}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Academic Year: ${enrollment.academicYearName}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Status: ${enrollment.status}",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = "Enrolled: ${formatDateString(enrollment.enrollmentDate)}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AttendanceBar(percentage: Float, label: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .background(
                        color = when {
                            percentage >= 90f -> Color.Green
                            percentage >= 75f -> Color(0xFFFFA500) // Orange
                            else -> Color.Red
                        }
                    )
            )
        }
    }
}

@Composable
fun StudentFinancialTab(financialSummaryState: StudentViewModel.FinancialSummaryState) {
    when (financialSummaryState) {
        is StudentViewModel.FinancialSummaryState.Loading -> {
            LoadingIndicator()
        }
        is StudentViewModel.FinancialSummaryState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading financial data: ${financialSummaryState.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        is StudentViewModel.FinancialSummaryState.Success -> {
            val summary = financialSummaryState.summary
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Account Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Account Status",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (summary.hasAccount) {
                            val balanceColor = when {
                                summary.currentBalance > 0 -> Color.Green
                                summary.currentBalance < 0 -> Color.Red
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Current Balance:",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = formatCurrency(summary.currentBalance),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = balanceColor
                                )
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Paid:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatCurrency(summary.totalPaid),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total Charged:",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = formatCurrency(summary.totalCharged),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Text(
                                text = "No financial account found for this student",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                // Recent Transactions Card
                if (summary.hasAccount && summary.recentTransactions.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Recent Transactions",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            TransactionsList(transactions = summary.recentTransactions)
                        }
                    }
                }
            }
        }
        else -> {} // Idle state, do nothing
    }
}

@Composable
fun TransactionsList(transactions: List<Map<String, Any>>) {
    Column {
        transactions.forEach { transaction ->
            TransactionItem(transaction)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun TransactionItem(transaction: Map<String, Any>) {
    val date = transaction["date"] as? String ?: ""
    val amount = (transaction["amount"] as? Double) ?: 0.0
    val description = transaction["description"] as? String ?: ""
    val type = transaction["type"] as? String ?: ""

    val amountColor = when (type.lowercase()) {
        "payment", "credit" -> Color.Green
        "charge", "debit" -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatDateString(date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = formatCurrency(amount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
fun StudentDocumentsTab(documentsState: StudentViewModel.DocumentsState) {
    when (documentsState) {
        is StudentViewModel.DocumentsState.Loading -> {
            LoadingIndicator()
        }
        is StudentViewModel.DocumentsState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error loading documents: ${documentsState.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        is StudentViewModel.DocumentsState.Success -> {
            val documents = documentsState.documents
            val scrollState = rememberScrollState()

            if (documents.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.InsertDriveFile,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No documents found",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload new documents using the add button",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp)
                ) {
                    DocumentsByType(documents = documents)
                }
            }
        }
        else -> {} // Idle state, do nothing
    }
}

@Composable
fun DocumentsByType(documents: List<StudentDocument>) {
    val groupedDocuments = documents.groupBy { it.documentType }

    groupedDocuments.forEach { (type, docsOfType) ->
        Text(
            text = formatDocumentType(type.toString()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )

        DocumentsList(documents = docsOfType)
    }
}

@Composable
fun DocumentsList(documents: List<StudentDocument>) {
    Column {
        documents.forEach { document ->
            DocumentItem(document)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DocumentItem(document: StudentDocument) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Document Icon
            Icon(
                imageVector = getDocumentIcon(document.documentType),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Document Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = document.fileName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                document.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        containerColor = getDocumentStatusColor(document.status)
                    ) {
                        Text(
                            text = document.status.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Uploaded: ${formatDateString(document.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // View Button
            IconButton(onClick = { /* Open document */ }) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "View Document"
                )
            }
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = valueColor
            )
        }
    }
}

// Helper Functions
fun formatDateString(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
        localDateTime.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

fun formatCurrency(amount: Double): String {
    return String.format("$%.2f", amount)
}

fun formatDocumentType(type: String): String {
    return type.replace("_", " ").lowercase().split(" ").joinToString(" ") { it.capitalize() }
}

@Composable
fun getDocumentIcon(documentType: DocumentType): ImageVector {
    return when (documentType) {
        DocumentType.BIRTH_CERTIFICATE -> Icons.Default.Assignment
        DocumentType.PASSPORT -> Icons.Default.Book
        DocumentType.NATIONAL_ID -> Icons.Default.AccountBox
        DocumentType.SCHOOL_REPORT -> Icons.Default.Assessment
        DocumentType.MEDICAL_RECORD -> Icons.Default.MedicalServices
        DocumentType.VACCINATION_RECORD -> Icons.Default.HealthAndSafety
        DocumentType.GUARDIAN_ID -> Icons.Default.SupervisorAccount
        DocumentType.TRANSFER_LETTER -> Icons.Default.Send
        DocumentType.ADMISSION_LETTER -> Icons.Default.Mail
        DocumentType.OTHER -> Icons.Default.InsertDriveFile
        else -> Icons.Default.InsertDriveFile // For any future document types
    }
}

@Composable
fun getDocumentStatusColor(status: DocumentStatus): Color {
    return when (status) {
        DocumentStatus.VERIFIED -> Color.Green
        DocumentStatus.PENDING -> Color(0xFFFFA500) // Orange
        DocumentStatus.REJECTED -> Color.Red
        DocumentStatus.EXPIRED -> Color.Gray
        else -> Color.Gray // For any future status types
    }
}