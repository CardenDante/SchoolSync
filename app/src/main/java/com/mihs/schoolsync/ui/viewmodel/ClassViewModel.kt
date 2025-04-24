// ClassViewModel.kt
package com.mihs.schoolsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.ClassSection
import com.mihs.schoolsync.data.repository.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassViewModel @Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {

    private val _classes = MutableStateFlow<List<ClassSection>>(emptyList())
    val classes: StateFlow<List<ClassSection>> = _classes.asStateFlow()

    private val _classSection = MutableStateFlow<ClassSection?>(null)
    val classSection: StateFlow<ClassSection?> = _classSection.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchClasses() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = classRepository.getClasses()
                _classes.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchClassSection(classSectionId: Int) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = classRepository.getClassSection(classSectionId)
                _classSection.value = result

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