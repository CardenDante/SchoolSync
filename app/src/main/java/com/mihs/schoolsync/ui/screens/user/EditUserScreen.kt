package com.mihs.schoolsync.ui.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.UserUpdateRequest
import com.mihs.schoolsync.ui.viewmodel.UserViewModel
import com.mihs.schoolsync.utils.InputValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    userId: Int,
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val selectedUser by viewModel.selectedUser.collectAsState()
    val operationState by viewModel.userOperationState.collectAsState()
    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Initialize fields with user data when it's loaded
    LaunchedEffect(userId) {
        viewModel.fetchUserById(userId)
    }

    // Update fields when user data is available
    LaunchedEffect(selectedUser) {
        selectedUser?.let {
            fullName = it.fullName
            email = it.email
            phoneNumber = it.phoneNumber ?: ""
            isActive = it.isActive
        }
    }

    // Handle operation state changes
    LaunchedEffect(operationState) {
        if (operationState is UserViewModel.UserOperationState.Success &&
            (operationState as UserViewModel.UserOperationState.Success).operation == UserViewModel.Operation.UPDATE) {
            // Navigate back after successful update
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit User") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
                .verticalScroll(scrollState)
        ) {
            selectedUser?.let { user ->
                // Form Fields
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = if (it.isNotBlank()) null else "Full name is required"
                    },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = fullNameError != null,
                    supportingText = {
                        fullNameError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Full Name"
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (InputValidator.isValidEmail(it)) null else "Invalid email format"
                    },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = {
                        emailError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Number"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active Status Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account Status",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        thumbContent = {
                            Icon(
                                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isActive) "Active" else "Inactive",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        // Validate fields
                        fullNameError = if (fullName.isNotBlank()) null else "Full name is required"
                        emailError = if (InputValidator.isValidEmail(email)) null else "Invalid email format"

                        // If valid, update user
                        if (fullNameError == null && emailError == null) {
                            viewModel.updateUser(
                                userId,
                                UserUpdateRequest(
                                    fullName = fullName,
                                    email = email,
                                    phoneNumber = phoneNumber.ifBlank { null },
                                    isActive = isActive
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = operationState !is UserViewModel.UserOperationState.Loading
                ) {
                    if (operationState is UserViewModel.UserOperationState.Loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Save Changes")
                    }
                }
            } ?: run {
                // Loading or error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (operationState is UserViewModel.UserOperationState.Loading) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = "Failed to load user data",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Error message
            if (operationState is UserViewModel.UserOperationState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (operationState as UserViewModel.UserOperationState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}