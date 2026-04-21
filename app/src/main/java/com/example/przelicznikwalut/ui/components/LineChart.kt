package com.example.przelicznikwalut.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun LineChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    if (data.isEmpty()) return

    val values = data.map { it.second }
    val minVal = values.minOrNull() ?: 0.0
    val maxVal = values.maxOrNull() ?: 0.0
    val range = (maxVal - minVal).coerceAtLeast(0.0001)

    val density = LocalDensity.current
    val labelFontSize = 10.sp
    val labelFontSizePx = with(density) { labelFontSize.toPx() }
    val paddingLeft = with(density) { 50.dp.toPx() }
    val paddingBottom = with(density) { 30.dp.toPx() }
    val paddingTop = with(density) { 10.dp.toPx() }
    val paddingRight = with(density) { 10.dp.toPx() }

    Canvas(modifier = modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(8.dp)
    ) {
        val width = size.width - paddingLeft - paddingRight
        val height = size.height - paddingBottom - paddingTop
        val spacing = width / (data.size - 1).coerceAtLeast(1)

        // Podpisy osi Y i siatka
        val yStep = 4
        for (i in 0..yStep) {
            val yValue = minVal + (range * i / yStep)
            val yPos = paddingTop + height - (i.toFloat() / yStep * height)
            
            drawLine(
                color = gridColor,
                start = Offset(paddingLeft, yPos),
                end = Offset(paddingLeft + width, yPos),
                strokeWidth = 1.dp.toPx()
            )

            drawContext.canvas.nativeCanvas.drawText(
                String.format(Locale.getDefault(), "%.4f", yValue),
                5f,
                yPos + labelFontSizePx / 3,
                android.graphics.Paint().apply {
                    color = textColor.hashCode()
                    textSize = labelFontSizePx
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        // Podpisy osi X
        if (data.size >= 2) {
            val indices = listOf(0, data.size / 2, data.size - 1)
            indices.distinct().forEach { index ->
                val xPos = paddingLeft + (index * spacing)
                val date = data[index].first
                
                drawContext.canvas.nativeCanvas.drawText(
                    date,
                    xPos - (if (index == 0) 0f else if (index == data.size - 1) 80f else 40f),
                    size.height - 5f,
                    android.graphics.Paint().apply {
                        color = textColor.hashCode()
                        textSize = labelFontSizePx
                    }
                )
            }
        }

        // Ścieżka
        val path = Path()
        values.forEachIndexed { index, value ->
            val x = paddingLeft + (index * spacing)
            val normalizedValue = (value - minVal) / range
            val y = paddingTop + height - (normalizedValue.toFloat() * height)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
