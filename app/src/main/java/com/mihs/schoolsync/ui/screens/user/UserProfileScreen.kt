package com.mihs.schoolsync.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.User
import com.mihs.schoolsync.ui.viewmodel.UserViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onEditProfile: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfile) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit Profile"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentUser?.let { user ->
                // Profile Header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.fullName.first().toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user.fullName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Simple row for roles
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = user.roles.joinToString(", ") { it.capitalize() },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Personal Information Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Personal Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            icon = Icons.Default.Person,
                            label = "Full Name",
                            value = user.fullName
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.Email,
                            label = "Email",
                            value = user.email
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.AccountCircle,
                            label = "Username",
                            value = user.username
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.Phone,
                            label = "Phone Number",
                            value = user.phoneNumber
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.Badge,
                            label = "User Type",
                            value = user.userType.capitalize()
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "Status",
                            value = user.status.capitalize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Account Information Card
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Account Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ProfileInfoItem(
                            icon = Icons.Default.VerifiedUser,
                            label = "Account Verified",
                            value = if (user.isVerified) "Yes" else "No"
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.RadioButtonChecked,
                            label = "Account Active",
                            value = if (user.isActive) "Yes" else "No"
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.Security,
                            label = "Roles",
                            value = user.roles.joinToString(", ") { it.capitalize() }
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.SupervisorAccount,
                            label = "Super User",
                            value = if (user.isSuperuser) "Yes" else "No"
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.Fingerprint,
                            label = "UUID",
                            value = user.uuid
                        )

                        ProfileInfoItem(
                            icon = Icons.Default.CalendarToday,
                            label = "Created On",
                            value = formatDateString(user.createdAt)
                        )

                        user.lastLogin?.let {
                            ProfileInfoItem(
                                icon = Icons.Default.AccessTime,
                                label = "Last Login",
                                value = formatDateString(it)
                            )
                        }

                        user.updatedAt?.let {
                            ProfileInfoItem(
                                icon = Icons.Default.Update,
                                label = "Last Updated",
                                value = formatDateString(it)
                            )
                        }
                    }
                }
            } ?: run {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    icon: ImageVector,
    label: String,
    value: String?
) {
    val displayValue = value ?: "Not provided"

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
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = displayValue,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// Extension function to capitalize first letter safely
fun String?.capitalize(): String {
    return this?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } ?: "Not provided"
}

// Helper function to format date strings
fun formatDateString(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        date.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
    } catch (e: Exception) {
        try {
            // Try an alternative format if the first one fails
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val dateTime = LocalDateTime.parse(dateString, formatter)
            dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a"))
        } catch (e2: Exception) {
            // If all parsing fails, return the original string
            dateString
        }
    }
}