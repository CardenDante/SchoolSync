// AttendanceColors.kt
package com.mihs.schoolsync.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getPresentColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun getAbsentColor(): Color = MaterialTheme.colorScheme.error

@Composable
fun getLateColor(): Color = MaterialTheme.colorScheme.tertiary

@Composable
fun getExcusedColor(): Color = MaterialTheme.colorScheme.secondary