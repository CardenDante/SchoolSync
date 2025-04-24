// CourseViewModel.kt
package com.mihs.schoolsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.CourseOffering
import com.mihs.schoolsync.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _courses = MutableStateFlow<List<CourseOffering>>(emptyList())
    val courses: StateFlow<List<CourseOffering>> = _courses.asStateFlow()

    private val _classCourses = MutableStateFlow<List<CourseOffering>>(emptyList())
    val classCourses: StateFlow<List<CourseOffering>> = _classCourses.asStateFlow()

    private val _courseOffering = MutableStateFlow<CourseOffering?>(null)
    val courseOffering: StateFlow<CourseOffering?> = _courseOffering.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = courseRepository.getCourses()
                _courses.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchClassCourses(classSectionId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = courseRepository.getCoursesForClass(classSectionId)
                _classCourses.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchCourseOffering(courseOfferingId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = courseRepository.getCourseOffering(courseOfferingId)
                _courseOffering.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}