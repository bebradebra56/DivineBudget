package com.divinebudget.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.data.entity.Goal
import com.divinebudget.app.ui.theme.*

@Composable
fun GoalCard(
    goal: Goal,
    currency: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val progress: Float = remember(goal.currentAmount, goal.targetAmount) {
        if (goal.targetAmount > 0) {
            (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
        } else {
            0f
        }
    }

    EgyptianCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Goal name
            Text(
                text = goal.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Gold
            )

            // Pyramid progress indicator
            PyramidProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            // Progress percentage
            Text(
                text = "${(progress * 100).toInt()}% Complete",
                style = MaterialTheme.typography.titleMedium,
                color = Turquoise,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Amount info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$currency${String.format("%.2f", goal.currentAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Target",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$currency${String.format("%.2f", goal.targetAmount)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
            }

            if (goal.dailyContribution > 0) {
                Text(
                    text = "Daily: $currency${String.format("%.2f", goal.dailyContribution)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LapisBlue
                )
            }
        }
    }
}

@Composable
fun PyramidProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic), label = "pyramid_progress"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        
        // Draw pyramid outline
        val pyramidPath = Path().apply {
            moveTo(width / 2f, 0f)
            lineTo(0f, height)
            lineTo(width, height)
            close()
        }
        
        drawPath(
            path = pyramidPath,
            color = Gold.copy(alpha = 0.3f),
            style = Stroke(width = 4f)
        )
        
        // Draw progress fill (from bottom to top)
        val fillHeight = height * animatedProgress
        val fillPath = Path().apply {
            val topY = height - fillHeight
            val topWidth = width * (1f - animatedProgress)
            
            moveTo(width / 2f - topWidth / 2f, topY)
            lineTo(width / 2f + topWidth / 2f, topY)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        
        drawPath(
            path = fillPath,
            color = Gold.copy(alpha = 0.6f)
        )
        
        // Draw decorative lines
        for (i in 1..4) {
            val y = height * i / 5f
            val lineWidth = width * (1f - i / 5f)
            drawLine(
                color = Gold.copy(alpha = 0.4f),
                start = Offset(width / 2f - lineWidth / 2f, y),
                end = Offset(width / 2f + lineWidth / 2f, y),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )
        }
    }
}

