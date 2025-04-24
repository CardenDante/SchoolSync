// Create a TimeUtils.kt file with conversion methods
package com.mihs.schoolsync.utils

import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime
import java.time.LocalDateTime as JavaLocalDateTime
import java.time.YearMonth as JavaYearMonth
import org.threeten.bp.LocalDate as ThreetenLocalDate
import org.threeten.bp.LocalTime as ThreetenLocalTime
import org.threeten.bp.LocalDateTime as ThreetenLocalDateTime
import org.threeten.bp.YearMonth as ThreetenYearMonth

// Convert from ThreeTenBP to Java time
fun ThreetenLocalDate.toJavaLocalDate(): JavaLocalDate =
    JavaLocalDate.of(year, monthValue, dayOfMonth)

fun ThreetenLocalTime.toJavaLocalTime(): JavaLocalTime =
    JavaLocalTime.of(hour, minute, second, nano)

fun ThreetenLocalDateTime.toJavaLocalDateTime(): JavaLocalDateTime =
    JavaLocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second, nano)

fun ThreetenYearMonth.toJavaYearMonth(): JavaYearMonth =
    JavaYearMonth.of(year, monthValue)

// Convert from Java time to ThreeTenBP
fun JavaLocalDate.toThreetenLocalDate(): ThreetenLocalDate =
    ThreetenLocalDate.of(year, monthValue, dayOfMonth)

fun JavaLocalTime.toThreetenLocalTime(): ThreetenLocalTime =
    ThreetenLocalTime.of(hour, minute, second, nano)

fun JavaLocalDateTime.toThreetenLocalDateTime(): ThreetenLocalDateTime =
    ThreetenLocalDateTime.of(year, month.value, dayOfMonth, hour, minute, second, nano)

fun JavaYearMonth.toThreetenYearMonth(): ThreetenYearMonth =
    ThreetenYearMonth.of(year, month.value)