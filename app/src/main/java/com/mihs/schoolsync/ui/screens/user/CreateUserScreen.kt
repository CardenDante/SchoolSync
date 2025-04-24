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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.UserCreateRequest
import com.mihs.schoolsync.ui.viewmodel.UserViewModel
import com.mihs.schoolsync.utils.InputValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val operationState by viewModel.userOperationState.collectAsState()
    val scrollState = rememberScrollState()

    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("STUDENT") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Handle operation state changes
    LaunchedEffect(operationState) {
        if (operationState is UserViewModel.UserOperationState.Success &&
            (operationState as UserViewModel.UserOperationState.Success).operation == UserViewModel.Operation.CREATE) {
            // Navigate back after successful creation
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New User") },
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
            // Form Fields
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = if (it.length >= 3) null else "Username must be at least 3 characters"
                },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = usernameError != null,
                supportingText = {
                    usernameError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Username"
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text("Phone Number (Optional)") },
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

            // User Type Dropdown
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { /* Will be handled by exposedDropdownMenu */ }
            ) {
                OutlinedTextField(
                    value = userType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("User Type") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "User Type"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = false,
                    onDismissRequest = {}
                ) {
                    val userTypes = listOf("STUDENT", "TEACHER", "ADMIN", "STAFF")
                    userTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.capitalize()) },
                            onClick = {
                                userType = type
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = when {
                        it.length < 8 -> "Password must be at least 8 characters"
                        !it.contains(Regex("[A-Z]")) -> "Password must contain an uppercase letter"
                        !it.contains(Regex("[a-z]")) -> "Password must contain a lowercase letter"
                        !it.contains(Regex("\\d")) -> "Password must contain a number"
                        else -> null
                    }

                    // Update confirm password error if needed
                    if (confirmPassword.isNotEmpty()) {
                        confirmPasswordError = if (it == confirmPassword) null else "Passwords do not match"
                    }
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = if (it == password) null else "Passwords do not match"
                },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = {
                    confirmPasswordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Confirm Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide Password" else "Show Password"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Create Button
            Button(
                onClick = {
                    // Validate all fields
                    usernameError = if (username.length >= 3) null else "Username must be at least 3 characters"
                    fullNameError = if (fullName.isNotBlank()) null else "Full name is required"
                    emailError = if (InputValidator.isValidEmail(email)) null else "Invalid email format"

                    val passwordValidation = when {
                        password.length < 8 -> "Password must be at least 8 characters"
                        !password.contains(Regex("[A-Z]")) -> "Password must contain an uppercase letter"
                        !password.contains(Regex("[a-z]")) -> "Password must contain a lowercase letter"
                        !password.contains(Regex("\\d")) -> "Password must contain a number"
                        else -> null
                    }
                    passwordError = passwordValidation

                    confirmPasswordError = if (password == confirmPassword) null else "Passwords do not match"

                    // If all validations pass, create user
                    if (usernameError == null && fullNameError == null && emailError == null &&
                        passwordError == null && confirmPasswordError == null) {

                        viewModel.createUser(
                            UserCreateRequest(
                                username = username,
                                fullName = fullName,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                phoneNumber = phoneNumber.ifBlank { null },
                                userType = userType
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
                    Text("Create User")
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