package com.mihs.schoolsync.ui.screens.student

import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.StudentDetail
import com.mihs.schoolsync.data.models.StudentStatus
import com.mihs.schoolsync.ui.components.EmptyStateMessage
import com.mihs.schoolsync.ui.components.LoadingIndicator
import com.mihs.schoolsync.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentViewModel = hiltViewModel(),
    onStudentClick: (Int) -> Unit,
    onAddStudentClick: () -> Unit,
    onFilterClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val studentListState by viewModel.studentListState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var currentPage by remember { mutableStateOf(1) }

    // Fetch students when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.getStudents(page = currentPage)
    }

    // Fetch students when search query changes
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.getStudents(studentId = searchQuery, page = 1)
            currentPage = 1
        } else {
            viewModel.getStudents(page = currentPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onFilterClick) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                    IconButton(onClick = onAddStudentClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Student"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddStudentClick) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Student")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    viewModel.getStudents(studentId = searchQuery, page = 1)
                    currentPage = 1
                },
                placeholder = "Search by Student ID",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Student List Content
            when (val state = studentListState) {
                is StudentViewModel.StudentListState.Loading -> {
                    LoadingIndicator()
                }
                is StudentViewModel.StudentListState.Success -> {
                    if (state.response.items.isEmpty()) {
                        EmptyStateMessage(
                            message = "No students found",
                            icon = Icons.Default.People
                        )
                    } else {
                        Column {
                            // Student List
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                items(state.response.items) { student ->
                                    StudentListItem(
                                        student = student,
                                        onClick = { onStudentClick(student.id) }
                                    )
                                }
                            }

                            // Pagination
                            if (state.response.pages > 1) {
                                PaginationControls(
                                    currentPage = currentPage,
                                    totalPages = state.response.pages,
                                    onPageChange = { newPage ->
                                        currentPage = newPage
                                        viewModel.getStudents(
                                            studentId = if (searchQuery.isNotEmpty()) searchQuery else null,
                                            page = newPage
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is StudentViewModel.StudentListState.ClassSectionSuccess -> {
                    if (state.students.isEmpty()) {
                        EmptyStateMessage(
                            message = "No students in this class section",
                            icon = Icons.Default.People
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(state.students) { student ->
                                StudentListItem(
                                    student = student,
                                    onClick = { onStudentClick(student.id) }
                                )
                            }
                        }
                    }
                }
                is StudentViewModel.StudentListState.Error -> {
                    EmptyStateMessage(
                        message = "Error: ${state.message}",
                        icon = Icons.Default.Error
                    )
                }
                else -> {} // Idle state, do nothing
            }
        }
    }
}

@Composable
fun StudentListItem(
    student: StudentDetail,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student Icon with Status Color
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(getStatusColor(student.status).copy(alpha = 0.1f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = getStatusColor(student.status),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Student Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.studentId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                student.currentClass?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = student.status.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = getStatusColor(student.status)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Badge(
                        containerColor = if (student.isActive) Color.Green else Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = if (student.isActive) "Active" else "Inactive",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Page")
        }

        Text(
            text = "$currentPage of $totalPages",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages
        ) {
            Icon(Icons.Default.NavigateNext, contentDescription = "Next Page")
        }
    }
}

@Composable
fun getStatusColor(status: StudentStatus): Color {
    return when (status) {
        StudentStatus.ACTIVE -> Color.Green
        StudentStatus.PENDING -> Color(0xFFFFA500) // Orange
        StudentStatus.GRADUATED -> Color.Blue
        StudentStatus.WITHDRAWN -> Color.Gray
        StudentStatus.SUSPENDED -> Color.Red
        StudentStatus.EXPELLED -> Color.Red.copy(alpha = 0.7f)
        StudentStatus.TRANSFERRED -> Color.Cyan
        StudentStatus.DECEASED -> Color.Black
    }
}