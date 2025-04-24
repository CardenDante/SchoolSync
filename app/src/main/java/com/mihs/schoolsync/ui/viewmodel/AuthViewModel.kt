package com.mihs.schoolsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.UserResponse
import com.mihs.schoolsync.data.repository.AuthRepository
import com.mihs.schoolsync.utils.NetworkError
import com.mihs.schoolsync.utils.Result
import com.mihs.schoolsync.utils.TokenManager
import com.mihs.schoolsync.utils.handleNetworkError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser.asStateFlow()

    init {
        // Check if user is logged in and attempt to refresh token
        viewModelScope.launch {
            if (tokenManager.isTokenAvailable()) {
                refreshToken()
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val result = authRepository.login(email, password)
                when (result) {
                    is Result.Success -> {
                        _currentUser.value = result.data.user
                        _loginState.value = AuthState.Success
                    }
                    is Result.Error -> {
                        _loginState.value = AuthState.Error(
                            handleNetworkError(Throwable(result.message))
                        )
                    }
                    Result.Loading -> _loginState.value = AuthState.Loading
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(handleNetworkError(e))
            }
        }
    }

    fun register(username: String, email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val result = authRepository.register(username, email, password, fullName)
                _registerState.value = when (result) {
                    is Result.Success -> AuthState.Success
                    is Result.Error -> AuthState.Error(
                        handleNetworkError(Throwable(result.message))
                    )
                    Result.Loading -> AuthState.Loading
                }
            } catch (e: Exception) {
                _registerState.value = AuthState.Error(handleNetworkError(e))
            }
        }
    }

    private suspend fun refreshToken() {
        try {
            val result = authRepository.refreshAccessToken()
            if (result is Result.Success) {
                _currentUser.value = result.data.user
            } else {
                // Token refresh failed, need to login again
                tokenManager.clearTokens()
            }
        } catch (e: Exception) {
            // Token refresh failed, need to login again
            tokenManager.clearTokens()
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _currentUser.value = null
            _loginState.value = AuthState.Idle
            _registerState.value = AuthState.Idle
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        return tokenManager.isTokenAvailable()
    }
}

// Sealed class for authentication states
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val error: NetworkError) : AuthState()
}