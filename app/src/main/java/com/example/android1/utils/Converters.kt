package com.example.android1.utils

import java.text.SimpleDateFormat
import kotlin.math.roundToInt

private const val PRESSURE_CONSTANT = 0.75
private const val TIME_CONSTANT = 1000L
private const val TIME_PATTERN = "HH:mm"

private val windDirections = arrayOf(
    "North ↓", "North-East ↙", "East ←", "South-East ↖",
    "South ↑", "South-West ↗", "West →", "North-West ↘"
)

fun Int.convertPressureIntoMmHg(): Int =
    (this * PRESSURE_CONSTANT).roundToInt()

fun Int.convertWindAngleIntoDirection(): String {
    val index = (this / 45).toDouble().roundToInt() % 8
    return windDirections[index]
}

fun Int.convertMillisecondsToHoursAndMinutes(): String =
    SimpleDateFormat(TIME_PATTERN).format(this.toLong() * TIME_CONSTANT)
