package com.divinebudget.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.R
import com.divinebudget.app.ui.components.EgyptianCard
import com.divinebudget.app.ui.components.GlowingGoldCard
import com.divinebudget.app.ui.components.TransactionItem
import com.divinebudget.app.ui.theme.*
import com.divinebudget.app.viewmodel.OverviewViewModel

@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel,
    currency: String,
    onAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToGoals: () -> Unit
) {
    val balance by viewModel.balance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val activeGoals by viewModel.activeGoals.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "chest_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "glow_alpha"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = Gold,
                contentColor = BlackObsidian
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction),
                    modifier = Modifier.scale(1.3f)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = stringResource(R.string.treasury),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Balance Card
            item {
                GlowingGoldCard {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "💰",
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.alpha(glowAlpha)
                        )
                        Text(
                            text = "Total Balance",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$currency${String.format("%.2f", balance)}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Gold
                        )
                    }
                }
            }

            // Income and Expense Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                                style = MaterialTheme.typography.titleLarge,
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
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }
                }
            }

            // Active Goals Section
            if (activeGoals.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏺 ${stringResource(R.string.goals)}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Turquoise
                        )
                        TextButton(onClick = onNavigateToGoals) {
                            Text("View All", color = LapisBlue)
                        }
                    }
                }

                item {
                    EgyptianCard {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            activeGoals.take(2).forEach { goal ->
                                val progress = remember(goal.currentAmount, goal.targetAmount) {
                                    if (goal.targetAmount > 0) {
                                        ((goal.currentAmount / goal.targetAmount) * 100).toInt().coerceIn(0, 100)
                                    } else {
                                        0
                                    }
                                }
                                val progressFloat = remember(goal.currentAmount, goal.targetAmount) {
                                    if (goal.targetAmount > 0) {
                                        (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                                    } else {
                                        0f
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = goal.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Gold
                                        )
                                        LinearProgressIndicator(
                                            progress = progressFloat,
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            color = Turquoise,
                                            trackColor = StoneBrown
                                        )
                                    }
                                    Text(
                                        text = "$progress%",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Turquoise,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recent Transactions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📜 Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LapisBlue
                    )
                    TextButton(onClick = onNavigateToTransactions) {
                        Text("View All", color = LapisBlue)
                    }
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    EgyptianCard {
                        Text(
                            text = "No transactions yet. Start by adding your first transaction!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(recentTransactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        currency = currency
                    )
                }
            }
        }
    }
}

