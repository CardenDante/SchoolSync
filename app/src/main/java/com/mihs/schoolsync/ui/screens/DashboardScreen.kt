package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mihs.schoolsync.R
import com.mihs.schoolsync.ui.components.*
import com.mihs.schoolsync.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    onAttendanceClick: () -> Unit,
    onStudentsClick: () -> Unit,
    onCoursesClick: () -> Unit,
    onFinancesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Student management actions - using the existing onStudentsClick instead of direct navigation
    val navigateToStudentList: () -> Unit = onStudentsClick

    val navigateToAddStudent: () -> Unit = onStudentsClick

    val navigateToStudentFilter: () -> Unit = onStudentsClick

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirm Logout") },
            text = { Text("Are you sure you want to logout from SchoolSync?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Image(
                        painter = painterResource(id = R.drawable.school_sync_logo),
                        contentDescription = "SchoolSync Logo",
                        modifier = Modifier.size(38.dp),
                        contentScale = ContentScale.Fit
                    )
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { onProfileClick() }) {
                        ProfileAvatar(
                            name = currentUser?.fullName ?: "User",
                            size = 32
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Text("Welcome back,", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = currentUser?.fullName ?: "User",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Today's Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                val studentCardModifier = Modifier
                    .weight(1f)
                    .clickable(onClick = navigateToStudentList)

                DashboardMetricCard(
                    title = "Students Present", value = "187",
                    icon = Icons.Outlined.People,
                    iconBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    iconColor = MaterialTheme.colorScheme.primary,
                    modifier = studentCardModifier
                )
                DashboardMetricCard(
                    title = "Classes Today", value = "12",
                    icon = Icons.Outlined.Class,
                    iconBackgroundColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                    iconColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardMetricCard(
                    title = "Payments Due", value = "8",
                    icon = Icons.Outlined.Payment,
                    iconBackgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                    iconColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                DashboardMetricCard(
                    title = "Documents", value = "3",
                    icon = Icons.Outlined.Description,
                    iconBackgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                    iconColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Main Features", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard(
                    "Student Management",
                    "Manage student records",
                    Icons.Outlined.People,
                    MaterialTheme.colorScheme.primary,
                    navigateToStudentList,
                    Modifier.weight(1f)
                )
                DashboardCard("Attendance", "Track attendance", Icons.Outlined.CheckCircle, MaterialTheme.colorScheme.secondary, onAttendanceClick, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardCard("Finance", "Fee payments", Icons.Outlined.AccountBalance, MaterialTheme.colorScheme.tertiary, onFinancesClick, Modifier.weight(1f))
                DashboardCard("Academics", "Manage subjects", Icons.Outlined.School, Color(0xFF2E7D32), onCoursesClick, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Quick Actions", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            QuickActionCard("View All Students", Icons.Outlined.Group, navigateToStudentList)
            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard("Register New Student", Icons.Outlined.PersonAdd, navigateToAddStudent)
            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard("Filter Students", Icons.Outlined.FilterList, navigateToStudentFilter)
            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard("Take Attendance", Icons.Outlined.HowToReg, onAttendanceClick)
            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard("Record Payment", Icons.Outlined.Payments, onFinancesClick)
            Spacer(modifier = Modifier.height(12.dp))

            QuickActionCard("Generate Reports", Icons.Outlined.Assessment) {}

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Outlined.Logout, contentDescription = "Logout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Go", tint = MaterialTheme.colorScheme.primary)
        }
    }
}