package com.divinebudget.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.R
import com.divinebudget.app.ui.components.EgyptianCard
import com.divinebudget.app.ui.theme.*
import com.divinebudget.app.viewmodel.StatisticsPeriod
import com.divinebudget.app.viewmodel.StatisticsType
import com.divinebudget.app.viewmodel.StatisticsViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    currency: String
) {
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val transactionsByCategory by viewModel.transactionsByCategory.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    
    val currentTotal = if (selectedType == StatisticsType.EXPENSE) totalExpense else totalIncome

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = stringResource(R.string.history_of_treasures),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Gold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Type selector (Income/Expense)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeChip(
                text = stringResource(R.string.income),
                icon = "☀️",
                selected = selectedType == StatisticsType.INCOME,
                onClick = { viewModel.setType(StatisticsType.INCOME) },
                color = IncomeGreen,
                modifier = Modifier.weight(1f)
            )
            TypeChip(
                text = stringResource(R.string.expense),
                icon = "⚖️",
                selected = selectedType == StatisticsType.EXPENSE,
                onClick = { viewModel.setType(StatisticsType.EXPENSE) },
                color = ExpenseRed,
                modifier = Modifier.weight(1f)
            )
        }

        // Period selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PeriodChip(
                text = "Week",
                selected = selectedPeriod == StatisticsPeriod.WEEK,
                onClick = { viewModel.setPeriod(StatisticsPeriod.WEEK) },
                modifier = Modifier.weight(1f)
            )
            PeriodChip(
                text = "Month",
                selected = selectedPeriod == StatisticsPeriod.MONTH,
                onClick = { viewModel.setPeriod(StatisticsPeriod.MONTH) },
                modifier = Modifier.weight(1f)
            )
            PeriodChip(
                text = "Year",
                selected = selectedPeriod == StatisticsPeriod.YEAR,
                onClick = { viewModel.setPeriod(StatisticsPeriod.YEAR) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Income vs Expense summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EgyptianCard(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "☀️", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = stringResource(R.string.income),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$currency${String.format("%.2f", totalIncome)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }
                    }

                    EgyptianCard(modifier = Modifier.weight(1f)) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⚖️", style = MaterialTheme.typography.headlineMedium)
                            Text(
                                text = stringResource(R.string.expense),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "$currency${String.format("%.2f", totalExpense)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }

            // Pie chart
            if (transactionsByCategory.isNotEmpty() && currentTotal > 0) {
                item {
                    EgyptianCard {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = if (selectedType == StatisticsType.EXPENSE) 
                                    "Expenses by Category" 
                                else 
                                    "Income by Category",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Gold
                            )
                            
                            Text(
                                text = "Total: $currency${String.format("%.2f", currentTotal)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selectedType == StatisticsType.EXPENSE) ExpenseRed else IncomeGreen
                            )
                            
                            PieChart(
                                data = transactionsByCategory.map { it.percentage },
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                // Category breakdown
                items(transactionsByCategory) { stat ->
                    EgyptianCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = getCategoryColor(transactionsByCategory.indexOf(stat)),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stat.category.icon,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Column {
                                    Text(
                                        text = stat.category.displayName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold
                                    )
                                    Text(
                                        text = "${stat.percentage.toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Text(
                                text = "$currency${String.format("%.2f", stat.amount)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedType == StatisticsType.EXPENSE) ExpenseRed else IncomeGreen
                            )
                        }
                    }
                }
            } else {
                item {
                    EgyptianCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(text = "📊", style = MaterialTheme.typography.displayLarge)
                            Text(
                                text = "No expense data for this period",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypeChip(
    text: String,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (selected) color else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, style = MaterialTheme.typography.titleMedium)
            Text(
                text = text,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun PeriodChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (selected) Gold else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (selected) BlackObsidian else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun PieChart(
    data: List<Float>,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }
    
    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
        )
    }

    Canvas(modifier = modifier) {
        val total = data.sum()
        if (total == 0f) return@Canvas
        
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = minOf(centerX, centerY) * 0.9f
        
        var startAngle = -90f
        
        data.forEachIndexed { index, value ->
            val sweepAngle = (value / total * 360f) * animatedProgress.value
            
            drawArc(
                color = getCategoryColor(index),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2)
            )
            
            // Draw border
            drawArc(
                color = Gold,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(centerX - radius, centerY - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = 4f)
            )
            
            startAngle += sweepAngle
        }
        
        // Draw center circle for donut effect
        drawCircle(
            color = StoneBrown,
            radius = radius * 0.5f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Gold,
            radius = radius * 0.5f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 3f)
        )
    }
}

fun getCategoryColor(index: Int): Color {
    val colors = listOf(
        Color(0xFFD4AF37), // Gold
        Color(0xFF4CA6A8), // Turquoise
        Color(0xFF1C5E8A), // Lapis Blue
        Color(0xFFB8664D), // Expense Red
        Color(0xFF8B4513), // Saddle Brown
        Color(0xFF2E8B57), // Sea Green
        Color(0xFF9370DB), // Medium Purple
        Color(0xFFCD853F), // Peru
        Color(0xFF20B2AA), // Light Sea Green
        Color(0xFFBC8F8F), // Rosy Brown
        Color(0xFF4682B4), // Steel Blue
        Color(0xFFDAA520)  // Goldenrod
    )
    return colors[index % colors.size]
}

