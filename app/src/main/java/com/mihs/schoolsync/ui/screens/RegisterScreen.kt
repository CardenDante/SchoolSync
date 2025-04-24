package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.R
import com.mihs.schoolsync.ui.viewmodel.AuthState
import com.mihs.schoolsync.ui.viewmodel.AuthViewModel
import com.mihs.schoolsync.utils.InputValidator
import com.mihs.schoolsync.utils.NetworkError

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val registerState by viewModel.registerState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        // App Logo
        Image(
            painter = painterResource(id = R.drawable.school_sync_logo),
            contentDescription = "SchoolSync Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Title
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Join SchoolSync today",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name TextField
        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameError = if (it.isNotBlank()) null
                else "Full name is required"
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
                    contentDescription = "Person Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = if (it.length >= 3) null
                else "Username must be at least 3 characters"
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
                    contentDescription = "Username Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (InputValidator.isValidEmail(it)) null
                else "Invalid email format"
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
                    contentDescription = "Email Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
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

                // Also update confirmPasswordError if needed
                if (confirmPassword.isNotEmpty()) {
                    confirmPasswordError = if (it == confirmPassword) null
                    else "Passwords do not match"
                }
            },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
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
                    contentDescription = "Password Icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "Hide" else "Show",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password TextField
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = if (it == password) null
                else "Passwords do not match"
            },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (confirmPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
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
                    contentDescription = "Confirm Password Icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "Hide" else "Show",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    validateAndRegister(
                        viewModel, username, email, fullName, password, confirmPassword,
                        setUsernameError = { usernameError = it },
                        setEmailError = { emailError = it },
                        setFullNameError = { fullNameError = it },
                        setPasswordError = { passwordError = it },
                        setConfirmPasswordError = { confirmPasswordError = it }
                    )
                }
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = {
                validateAndRegister(
                    viewModel, username, email, fullName, password, confirmPassword,
                    setUsernameError = { usernameError = it },
                    setEmailError = { emailError = it },
                    setFullNameError = { fullNameError = it },
                    setPasswordError = { passwordError = it },
                    setConfirmPasswordError = { confirmPasswordError = it }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = registerState !is AuthState.Loading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (registerState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    "Create Account",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Registration State Handling
        when (val state = registerState) {
            is AuthState.Error -> {
                val errorMessage = when (val error = state.error) {
                    is NetworkError.AuthenticationError -> error.message
                    is NetworkError.NetworkConnectionError -> "No internet connection"
                    is NetworkError.ServerError -> "Server error occurred"
                    is NetworkError.ValidationError -> "Invalid input"
                    NetworkError.UnknownError -> "An unknown error occurred"
                }

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
            is AuthState.Success -> {
                LaunchedEffect(Unit) {
                    onRegistrationSuccess()
                }
            }
            else -> {} // Idle state, do nothing
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Already have an account?",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onLoginClick) {
                Text(
                    "Sign In",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// Helper function to validate and register
private fun validateAndRegister(
    viewModel: AuthViewModel,
    username: String,
    email: String,
    fullName: String,
    password: String,
    confirmPassword: String,
    setUsernameError: (String?) -> Unit,
    setEmailError: (String?) -> Unit,
    setFullNameError: (String?) -> Unit,
    setPasswordError: (String?) -> Unit,
    setConfirmPasswordError: (String?) -> Unit
) {
    // Validate all fields
    setUsernameError(if (username.length >= 3) null else "Username must be at least 3 characters")
    setEmailError(if (InputValidator.isValidEmail(email)) null else "Invalid email format")
    setFullNameError(if (fullName.isNotBlank()) null else "Full name is required")

    val passwordValidation = when {
        password.length < 8 -> "Password must be at least 8 characters"
        !password.contains(Regex("[A-Z]")) -> "Password must contain an uppercase letter"
        !password.contains(Regex("[a-z]")) -> "Password must contain a lowercase letter"
        !password.contains(Regex("\\d")) -> "Password must contain a number"
        else -> null
    }
    setPasswordError(passwordValidation)

    setConfirmPasswordError(if (confirmPassword == password) null else "Passwords do not match")

    // Proceed with registration if no errors
    if (username.length >= 3 &&
        InputValidator.isValidEmail(email) &&
        fullName.isNotBlank() &&
        passwordValidation == null &&
        confirmPassword == password
    ) {
        viewModel.register(username, email, password, fullName)
    }
}