package com.mihs.schoolsync.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.components.ProfileAvatar
import com.mihs.schoolsync.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    viewModel: UserViewModel = hiltViewModel(),
    onEditClick: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedUser by viewModel.selectedUser.collectAsState()
    val operationState by viewModel.userOperationState.collectAsState()
    val scrollState = rememberScrollState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        viewModel.fetchUserById(userId)
    }

    // Handle successful operations
    LaunchedEffect(operationState) {
        if (operationState is UserViewModel.UserOperationState.Success) {
            when ((operationState as UserViewModel.UserOperationState.Success).operation) {
                UserViewModel.Operation.DELETE -> onNavigateBack()
                else -> {} // Handle other operations if needed
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete this user? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        selectedUser?.id?.let { viewModel.deleteUser(it) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    selectedUser?.let { user ->
                        IconButton(
                            onClick = { onEditClick(user.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit User"
                            )
                        }
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
            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                selectedUser?.let { user ->
                    // User Avatar and basic info
                    ProfileAvatar(
                        name = user.fullName,
                        size = 100
                    )

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.userType.capitalize(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Badge(
                            containerColor = if (user.isActive) Color.Green else Color.Red
                        ) {
                            Text(
                                text = if (user.isActive) "Active" else "Inactive",
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // User details
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "User Information",
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
                                value = user.phoneNumber ?: "Not provided"
                            )

                            ProfileInfoItem(
                                icon = Icons.Default.Badge,
                                label = "User Type",
                                value = user.userType.capitalize()
                            )

                            ProfileInfoItem(
                                icon = Icons.Default.Fingerprint,
                                label = "User ID",
                                value = user.id.toString()
                            )

                            ProfileInfoItem(
                                icon = Icons.Default.Info,
                                label = "UUID",
                                value = user.uuid
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Account status
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Account Status",
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
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (user.isActive) {
                            OutlinedButton(
                                onClick = { viewModel.deactivateUser(user.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Red
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Deactivate"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Deactivate")
                            }
                        } else {
                            Button(
                                onClick = { viewModel.activateUser(user.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Green
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Activate"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Activate")
                            }
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete")
                        }
                    }
                } ?: run {
                    // Loading or error state
                    if (operationState is UserViewModel.UserOperationState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Failed to load user details",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (operationState is UserViewModel.UserOperationState.Error) {
                                Text(
                                    text = (operationState as UserViewModel.UserOperationState.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.fetchUserById(userId) }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            // Operation loading indicator
            if (operationState is UserViewModel.UserOperationState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}