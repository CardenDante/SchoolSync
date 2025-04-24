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
import com.mihs.schoolsync.ui.components.ProfileAvatar
import com.mihs.schoolsync.ui.viewmodel.UserViewModel
import com.mihs.schoolsync.utils.InputValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val operationState by viewModel.userOperationState.collectAsState()
    val scrollState = rememberScrollState()

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Initialize fields with current user data when available
    LaunchedEffect(currentUser) {
        currentUser?.let {
            fullName = it.fullName
            email = it.email
            phoneNumber = it.phoneNumber ?: ""
        }
    }

    // Handle operation state changes
    LaunchedEffect(operationState) {
        if (operationState is UserViewModel.UserOperationState.Success &&
            (operationState as UserViewModel.UserOperationState.Success).operation == UserViewModel.Operation.UPDATE_PROFILE) {
            // Navigate back after successful update
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Avatar
            currentUser?.let {
                ProfileAvatar(
                    name = it.fullName,
                    size = 100
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

            // Submit Button
            Button(
                onClick = {
                    // Validate fields
                    fullNameError = if (fullName.isNotBlank()) null else "Full name is required"
                    emailError = if (InputValidator.isValidEmail(email)) null else "Invalid email format"

                    // If valid, update profile
                    if (fullNameError == null && emailError == null) {
                        viewModel.updateCurrentUser(
                            UserUpdateRequest(
                                fullName = fullName,
                                email = email,
                                phoneNumber = phoneNumber.ifBlank { null }
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