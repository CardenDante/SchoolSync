package com.mihs.schoolsync.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.*
import com.mihs.schoolsync.data.repository.StudentRepository
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _studentListState = MutableStateFlow<StudentListState>(StudentListState.Idle)
    val studentListState: StateFlow<StudentListState> = _studentListState.asStateFlow()

    private val _studentDetailState = MutableStateFlow<StudentDetailState>(StudentDetailState.Idle)
    val studentDetailState: StateFlow<StudentDetailState> = _studentDetailState.asStateFlow()

    private val _studentOperationState = MutableStateFlow<StudentOperationState>(StudentOperationState.Idle)
    val studentOperationState: StateFlow<StudentOperationState> = _studentOperationState.asStateFlow()

    private val _academicSummaryState = MutableStateFlow<AcademicSummaryState>(AcademicSummaryState.Idle)
    val academicSummaryState: StateFlow<AcademicSummaryState> = _academicSummaryState.asStateFlow()

    private val _financialSummaryState = MutableStateFlow<FinancialSummaryState>(FinancialSummaryState.Idle)
    val financialSummaryState: StateFlow<FinancialSummaryState> = _financialSummaryState.asStateFlow()

    private val _documentsState = MutableStateFlow<DocumentsState>(DocumentsState.Idle)
    val documentsState: StateFlow<DocumentsState> = _documentsState.asStateFlow()

    fun getStudents(
        studentId: String? = null,
        status: String? = null,
        isActive: Boolean? = null,
        admissionDateStart: String? = null,
        admissionDateEnd: String? = null,
        classSectionId: Int? = null,
        page: Int = 1,
        size: Int = 10
    ) {
        viewModelScope.launch {
            _studentListState.value = StudentListState.Loading
            try {
                val result = studentRepository.getStudents(
                    studentId, status, isActive,
                    admissionDateStart, admissionDateEnd,
                    classSectionId, page, size
                )
                _studentListState.value = when (result) {
                    is Result.Success -> StudentListState.Success(result.data)
                    is Result.Error -> StudentListState.Error(result.message)
                    Result.Loading -> StudentListState.Loading
                }
            } catch (e: Exception) {
                _studentListState.value = StudentListState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudent(studentId: Int) {
        viewModelScope.launch {
            _studentDetailState.value = StudentDetailState.Loading
            try {
                val result = studentRepository.getStudent(studentId)
                _studentDetailState.value = when (result) {
                    is Result.Success -> StudentDetailState.Success(result.data)
                    is Result.Error -> StudentDetailState.Error(result.message)
                    Result.Loading -> StudentDetailState.Loading
                }
            } catch (e: Exception) {
                _studentDetailState.value = StudentDetailState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudentByStudentId(studentId: String) {
        viewModelScope.launch {
            _studentDetailState.value = StudentDetailState.Loading
            try {
                val result = studentRepository.getStudentByStudentId(studentId)
                _studentDetailState.value = when (result) {
                    is Result.Success -> StudentDetailState.Success(result.data)
                    is Result.Error -> StudentDetailState.Error(result.message)
                    Result.Loading -> StudentDetailState.Loading
                }
            } catch (e: Exception) {
                _studentDetailState.value = StudentDetailState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun createStudent(studentCreateRequest: StudentCreateRequest) {
        viewModelScope.launch {
            _studentOperationState.value = StudentOperationState.Loading
            try {
                val result = studentRepository.createStudent(studentCreateRequest)
                _studentOperationState.value = when (result) {
                    is Result.Success -> StudentOperationState.Success(Operation.CREATE)
                    is Result.Error -> StudentOperationState.Error(result.message)
                    Result.Loading -> StudentOperationState.Loading
                }
            } catch (e: Exception) {
                _studentOperationState.value = StudentOperationState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun updateStudent(studentId: Int, studentUpdateRequest: StudentUpdateRequest) {
        viewModelScope.launch {
            _studentOperationState.value = StudentOperationState.Loading
            try {
                val result = studentRepository.updateStudent(studentId, studentUpdateRequest)
                _studentOperationState.value = when (result) {
                    is Result.Success -> StudentOperationState.Success(Operation.UPDATE)
                    is Result.Error -> StudentOperationState.Error(result.message)
                    Result.Loading -> StudentOperationState.Loading
                }
            } catch (e: Exception) {
                _studentOperationState.value = StudentOperationState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun updateStudentStatus(studentId: Int, statusUpdate: StudentStatusUpdateRequest) {
        viewModelScope.launch {
            _studentOperationState.value = StudentOperationState.Loading
            try {
                val result = studentRepository.updateStudentStatus(studentId, statusUpdate)
                _studentOperationState.value = when (result) {
                    is Result.Success -> StudentOperationState.Success(Operation.UPDATE_STATUS)
                    is Result.Error -> StudentOperationState.Error(result.message)
                    Result.Loading -> StudentOperationState.Loading
                }
            } catch (e: Exception) {
                _studentOperationState.value = StudentOperationState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudentAcademicSummary(studentId: Int) {
        viewModelScope.launch {
            _academicSummaryState.value = AcademicSummaryState.Loading
            try {
                val result = studentRepository.getStudentAcademicSummary(studentId)
                _academicSummaryState.value = when (result) {
                    is Result.Success -> AcademicSummaryState.Success(result.data)
                    is Result.Error -> AcademicSummaryState.Error(result.message)
                    Result.Loading -> AcademicSummaryState.Loading
                }
            } catch (e: Exception) {
                _academicSummaryState.value = AcademicSummaryState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudentFinancialSummary(studentId: Int) {
        viewModelScope.launch {
            _financialSummaryState.value = FinancialSummaryState.Loading
            try {
                val result = studentRepository.getStudentFinancialSummary(studentId)
                _financialSummaryState.value = when (result) {
                    is Result.Success -> FinancialSummaryState.Success(result.data)
                    is Result.Error -> FinancialSummaryState.Error(result.message)
                    Result.Loading -> FinancialSummaryState.Loading
                }
            } catch (e: Exception) {
                _financialSummaryState.value = FinancialSummaryState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudentDocuments(studentId: Int) {
        viewModelScope.launch {
            _documentsState.value = DocumentsState.Loading
            try {
                val result = studentRepository.getStudentDocuments(studentId)
                _documentsState.value = when (result) {
                    is Result.Success -> DocumentsState.Success(result.data)
                    is Result.Error -> DocumentsState.Error(result.message)
                    Result.Loading -> DocumentsState.Loading
                }
            } catch (e: Exception) {
                _documentsState.value = DocumentsState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun getStudentsByClassSection(classSectionId: Int) {
        viewModelScope.launch {
            _studentListState.value = StudentListState.Loading
            try {
                val result = studentRepository.getStudentsByClassSection(classSectionId)
                _studentListState.value = when (result) {
                    is Result.Success -> StudentListState.ClassSectionSuccess(result.data)
                    is Result.Error -> StudentListState.Error(result.message)
                    Result.Loading -> StudentListState.Loading
                }
            } catch (e: Exception) {
                _studentListState.value = StudentListState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun resetOperationState() {
        _studentOperationState.value = StudentOperationState.Idle
    }

    enum class Operation {
        CREATE, UPDATE, UPDATE_STATUS, DELETE
    }

    sealed class StudentListState {
        object Idle : StudentListState()
        object Loading : StudentListState()
        data class Success(val response: StudentListResponse) : StudentListState()
        data class ClassSectionSuccess(val students: List<StudentDetail>) : StudentListState()
        data class Error(val message: String) : StudentListState()
    }

    sealed class StudentDetailState {
        object Idle : StudentDetailState()
        object Loading : StudentDetailState()
        data class Success(val student: StudentDetail) : StudentDetailState()
        data class Error(val message: String) : StudentDetailState()
    }

    sealed class StudentOperationState {
        object Idle : StudentOperationState()
        object Loading : StudentOperationState()
        data class Success(val operation: Operation) : StudentOperationState()
        data class Error(val message: String) : StudentOperationState()
    }

    sealed class AcademicSummaryState {
        object Idle : AcademicSummaryState()
        object Loading : AcademicSummaryState()
        data class Success(val summary: StudentAcademicSummary) : AcademicSummaryState()
        data class Error(val message: String) : AcademicSummaryState()
    }

    sealed class FinancialSummaryState {
        object Idle : FinancialSummaryState()
        object Loading : FinancialSummaryState()
        data class Success(val summary: StudentFinancialSummary) : FinancialSummaryState()
        data class Error(val message: String) : FinancialSummaryState()
    }

    sealed class DocumentsState {
        object Idle : DocumentsState()
        object Loading : DocumentsState()
        data class Success(val documents: List<StudentDocument>) : DocumentsState()
        data class Error(val message: String) : DocumentsState()
    }
}