//// StudentViewModelAdapter.kt
//package com.mihs.schoolsync.ui.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.mihs.schoolsync.data.models.Student
//import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
//import com.mihs.schoolsync.utils.StudentAdapter
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
///**
// * Adapter ViewModel that provides the interface expected by the fee module
// * but delegates to the existing StudentViewModel
// */
//@HiltViewModel
//class StudentViewModelAdapter @Inject constructor(
//    private val studentViewModel: StudentViewModel
//) : ViewModel() {
//
//    private val _students = MutableStateFlow<com.mihs.schoolsync.utils.Result<List<Student>>>(com.mihs.schoolsync.utils.Result.Loading)
//    val students: StateFlow<com.mihs.schoolsync.utils.Result<List<Student>>> = _students.asStateFlow()
//
//    private val _student = MutableStateFlow<com.mihs.schoolsync.utils.Result<Student>>(com.mihs.schoolsync.utils.Result.Initial)
//    val student: StateFlow<com.mihs.schoolsync.utils.Result<Student>> = _student.asStateFlow()
//
//    init {
//        // Monitor the student list state from the original viewModel
//        viewModelScope.launch {
//            studentViewModel.studentListState.collectLatest { state ->
//                when (state) {
//                    is StudentViewModel.StudentListState.Success -> {
//                        val adaptedStudents = StudentAdapter.toFeeModuleStudents(state.response.items)
//                        _students.value = com.mihs.schoolsync.utils.Result.Success(adaptedStudents)
//                    }
//                    is StudentViewModel.StudentListState.ClassSectionSuccess -> {
//                        val adaptedStudents = StudentAdapter.toFeeModuleStudents(state.students)
//                        _students.value = com.mihs.schoolsync.utils.Result.Success(adaptedStudents)
//                    }
//                    is StudentViewModel.StudentListState.Loading -> {
//                        _students.value = com.mihs.schoolsync.utils.Result.Loading
//                    }
//                    is StudentViewModel.StudentListState.Error -> {
//                        _students.value = com.mihs.schoolsync.utils.Result.Error(state.message)
//                    }
//                    else -> {
//                        // No-op for Idle state
//                    }
//                }
//            }
//        }
//
//        // Monitor the student detail state from the original viewModel
//        viewModelScope.launch {
//            studentViewModel.studentDetailState.collectLatest { state ->
//                when (state) {
//                    is StudentViewModel.StudentDetailState.Success -> {
//                        val adaptedStudent = StudentAdapter.toFeeModuleStudent(state.student)
//                        _student.value = com.mihs.schoolsync.utils.Result.Success(adaptedStudent)
//                    }
//                    is StudentViewModel.StudentDetailState.Loading -> {
//                        _student.value = com.mihs.schoolsync.utils.Result.Loading
//                    }
//                    is StudentViewModel.StudentDetailState.Error -> {
//                        _student.value = com.mihs.schoolsync.utils.Result.Error(state.message)
//                    }
//                    else -> {
//                        // No-op for Idle state
//                    }
//                }
//            }
//        }
//    }
//
//    fun getStudents() {
//        studentViewModel.getStudents()
//    }
//
//    fun getStudent(studentId: Int) {
//        studentViewModel.getStudent(studentId)
//    }
//}