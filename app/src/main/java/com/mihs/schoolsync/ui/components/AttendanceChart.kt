// AttendanceChart.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SimpleLineChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) {
        PlaceholderChart(
            title = "No data available",
            modifier = modifier
        )
        return
    }

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val maxValue = data.maxOfOrNull { it.second } ?: 0f
            val minValue = data.minOfOrNull { it.second } ?: 0f
            val range = if (maxValue == minValue) 1f else maxValue - minValue

            val width = size.width
            val height = size.height
            val xStep = width / (data.size - 1)

            // Draw line chart
            val path = Path()

            data.forEachIndexed { index, (_, value) ->
                val x = index * xStep
                val y = height - (value - minValue) / range * height

                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }

                // Draw point
                drawCircle(
                    color = lineColor,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }

            // Draw line
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
        }

        // X-axis labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 204.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Only show first, middle and last label to avoid overcrowding
            if (data.size >= 3) {
                Text(
                    text = data.first().first,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = data[data.size / 2].first,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = data.last().first,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (data.size == 2) {
                Text(
                    text = data.first().first,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = data.last().first,
                    style = MaterialTheme.typography.bodySmall
                )
            } else if (data.size == 1) {
                Text(
                    text = data.first().first,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PieChart(
    data: List<Pair<String, Float>>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        PlaceholderChart(
            title = "No data available",
            modifier = modifier
        )
        return
    }

    val total = data.sumOf { it.second.toDouble() }.toFloat()
    if (total <= 0) {
        PlaceholderChart(
            title = "No data available",
            modifier = modifier
        )
        return
    }

    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )

    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        ) {
            var startAngle = 0f

            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = 360f * (value / total)

                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )

                startAngle += sweepAngle
            }
        }

        // Legend
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 220.dp)
        ) {
            data.forEachIndexed { index, (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = colors[index % colors.size],
                                shape = RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${(value / total * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceholderChart(
    title: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            )
            .height(250.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No chart data available",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}