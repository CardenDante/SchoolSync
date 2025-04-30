// FeeStructureViewModel.kt
package com.mihs.schoolsync.ui.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.repository.FeeStructureRepository
import com.mihs.schoolsync.ui.finance.models.*
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeeStructureViewModel @Inject constructor(
    private val feeStructureRepository: FeeStructureRepository
) : ViewModel() {

    // Fee structures list state
    private val _feeStructuresState = MutableStateFlow<FeeStructuresState>(FeeStructuresState.Idle)
    val feeStructuresState: StateFlow<FeeStructuresState> = _feeStructuresState.asStateFlow()

    // Single fee structure state
    private val _feeStructureState = MutableStateFlow<FeeStructureState>(FeeStructureState.Idle)
    val feeStructureState: StateFlow<FeeStructureState> = _feeStructureState.asStateFlow()

    // Create/Update state
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    /**
     * Get all fee structures
     */
    fun getFeeStructures(activeOnly: Boolean = false) {
        viewModelScope.launch {
            _feeStructuresState.value = FeeStructuresState.Loading

            try {
                val result = feeStructureRepository.getFeeStructures(activeOnly)
                _feeStructuresState.value = when (result) {
                    is Result.Success -> FeeStructuresState.Success(result.data)
                    is Result.Error -> FeeStructuresState.Error(result.message)
                    else -> FeeStructuresState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _feeStructuresState.value = FeeStructuresState.Error("Error fetching fee structures: ${e.message}")
            }
        }
    }

    /**
     * Get a specific fee structure by ID
     */
    fun getFeeStructure(structureId: Int) {
        viewModelScope.launch {
            _feeStructureState.value = FeeStructureState.Loading

            try {
                val result = feeStructureRepository.getFeeStructure(structureId)
                _feeStructureState.value = when (result) {
                    is Result.Success -> FeeStructureState.Success(result.data)
                    is Result.Error -> FeeStructureState.Error(result.message)
                    else -> FeeStructureState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _feeStructureState.value = FeeStructureState.Error("Error fetching fee structure: ${e.message}")
            }
        }
    }

    /**
     * Create a new fee structure
     */
    fun createFeeStructure(structure: FeeStructureCreateRequest, items: List<FeeItemCreateRequest>) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val result = feeStructureRepository.createFeeStructure(structure, items)
                _saveState.value = when (result) {
                    is Result.Success -> SaveState.Success(result.data)
                    is Result.Error -> SaveState.Error(result.message)
                    else -> SaveState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Error creating fee structure: ${e.message}")
            }
        }
    }

    /**
     * Update an existing fee structure
     */
    fun updateFeeStructure(structureId: Int, structure: FeeStructureUpdateRequest) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val result = feeStructureRepository.updateFeeStructure(structureId, structure)
                _saveState.value = when (result) {
                    is Result.Success -> SaveState.Success(result.data)
                    is Result.Error -> SaveState.Error(result.message)
                    else -> SaveState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Error updating fee structure: ${e.message}")
            }
        }
    }

    /**
     * Add a fee item to an existing structure
     */
    fun addFeeItem(structureId: Int, item: FeeItemCreateRequest) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val result = feeStructureRepository.addFeeItem(structureId, item)
                _saveState.value = when (result) {
                    is Result.Success -> SaveState.Success(result.data)
                    is Result.Error -> SaveState.Error(result.message)
                    else -> SaveState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Error adding fee item: ${e.message}")
            }
        }
    }

    /**
     * Delete a fee item
     */
    fun deleteFeeItem(itemId: Int) {
        viewModelScope.launch {
            _saveState.value = SaveState.Loading

            try {
                val result = feeStructureRepository.deleteFeeItem(itemId)
                _saveState.value = when (result) {
                    is Result.Success -> SaveState.Success(result.data)
                    is Result.Error -> SaveState.Error(result.message)
                    else -> SaveState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _saveState.value = SaveState.Error("Error deleting fee item: ${e.message}")
            }
        }
    }

    /**
     * Reset states
     */
    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }

    fun resetFeeStructureState() {
        _feeStructureState.value = FeeStructureState.Idle
    }

    /**
     * State classes for fee structure management
     */
    sealed class FeeStructuresState {
        object Idle : FeeStructuresState()
        object Loading : FeeStructuresState()
        data class Success(val structures: List<FeeStructureResponse>) : FeeStructuresState()
        data class Error(val message: String) : FeeStructuresState()
    }

    sealed class FeeStructureState {
        object Idle : FeeStructureState()
        object Loading : FeeStructureState()
        data class Success(val structure: FeeStructureResponse) : FeeStructureState()
        data class Error(val message: String) : FeeStructureState()
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Loading : SaveState()
        data class Success(val structure: FeeStructureResponse) : SaveState()
        data class Error(val message: String) : SaveState()
    }
}