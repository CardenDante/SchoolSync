package com.mihs.schoolsync.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.User
import com.mihs.schoolsync.data.models.UserCreateRequest
import com.mihs.schoolsync.data.models.UserUpdateRequest
import com.mihs.schoolsync.data.repository.UserRepository
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _userListState = MutableStateFlow<UserListState>(UserListState.Idle)
    val userListState: StateFlow<UserListState> = _userListState.asStateFlow()

    private val _userOperationState = MutableStateFlow<UserOperationState>(UserOperationState.Idle)
    val userOperationState: StateFlow<UserOperationState> = _userOperationState.asStateFlow()

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    init {
        fetchCurrentUser()
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                when (val result = userRepository.getCurrentUser()) {
                    is Result.Success -> {
                        _currentUser.value = result.data
                    }
                    is Result.Error -> {
                        Log.e("UserViewModel", "Error fetching current user: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Exception fetching current user", e)
            }
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            _userListState.value = UserListState.Loading
            try {
                when (val result = userRepository.getAllUsers()) {
                    is Result.Success -> {
                        _userListState.value = UserListState.Success(result.data)
                    }
                    is Result.Error -> {
                        _userListState.value = UserListState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userListState.value = UserListState.Loading
                    }
                }
            } catch (e: Exception) {
                _userListState.value = UserListState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun fetchUserById(userId: Int) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.getUserById(userId)) {
                    is Result.Success -> {
                        _selectedUser.value = result.data
                        _userOperationState.value = UserOperationState.Idle
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createUser(userCreateRequest: UserCreateRequest) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.createUser(userCreateRequest)) {
                    is Result.Success -> {
                        _userOperationState.value = UserOperationState.Success(Operation.CREATE)
                        fetchAllUsers()
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateUser(userId: Int, userUpdateRequest: UserUpdateRequest) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.updateUser(userId, userUpdateRequest)) {
                    is Result.Success -> {
                        if (_selectedUser.value?.id == userId) {
                            _selectedUser.value = result.data
                        }
                        _userOperationState.value = UserOperationState.Success(Operation.UPDATE)
                        fetchAllUsers()
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateCurrentUser(userUpdateRequest: UserUpdateRequest) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.updateCurrentUser(userUpdateRequest)) {
                    is Result.Success -> {
                        _currentUser.value = result.data
                        _userOperationState.value = UserOperationState.Success(Operation.UPDATE_PROFILE)
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.deleteUser(userId)) {
                    is Result.Success -> {
                        if (_selectedUser.value?.id == userId) {
                            _selectedUser.value = null
                        }
                        _userOperationState.value = UserOperationState.Success(Operation.DELETE)
                        fetchAllUsers()
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun activateUser(userId: Int) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.activateUser(userId)) {
                    is Result.Success -> {
                        if (_selectedUser.value?.id == userId) {
                            _selectedUser.value = result.data
                        }
                        _userOperationState.value = UserOperationState.Success(Operation.ACTIVATE)
                        fetchAllUsers()
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deactivateUser(userId: Int) {
        viewModelScope.launch {
            _userOperationState.value = UserOperationState.Loading
            try {
                when (val result = userRepository.deactivateUser(userId)) {
                    is Result.Success -> {
                        if (_selectedUser.value?.id == userId) {
                            _selectedUser.value = result.data
                        }
                        _userOperationState.value = UserOperationState.Success(Operation.DEACTIVATE)
                        fetchAllUsers()
                    }
                    is Result.Error -> {
                        _userOperationState.value = UserOperationState.Error(result.message)
                    }
                    is Result.Loading -> {
                        _userOperationState.value = UserOperationState.Loading
                    }
                }
            } catch (e: Exception) {
                _userOperationState.value = UserOperationState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun resetOperationState() {
        _userOperationState.value = UserOperationState.Idle
    }

    enum class Operation {
        CREATE, UPDATE, DELETE, ACTIVATE, DEACTIVATE, UPDATE_PROFILE
    }

    sealed class UserListState {
        object Idle : UserListState()
        object Loading : UserListState()
        data class Success(val users: List<User>) : UserListState()
        data class Error(val message: String) : UserListState()
    }

    sealed class UserOperationState {
        object Idle : UserOperationState()
        object Loading : UserOperationState()
        data class Success(val operation: Operation) : UserOperationState()
        data class Error(val message: String) : UserOperationState()
    }
}