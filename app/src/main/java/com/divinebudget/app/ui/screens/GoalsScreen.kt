package com.divinebudget.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.R
import com.divinebudget.app.ui.components.EgyptianCard
import com.divinebudget.app.ui.components.GoalCard
import com.divinebudget.app.ui.theme.*
import com.divinebudget.app.viewmodel.GoalViewModel
import com.divinebudget.app.data.entity.Goal

@Composable
fun GoalsScreen(
    viewModel: GoalViewModel,
    currency: String,
    onAddGoal: () -> Unit,
    onEditGoal: (Goal) -> Unit
) {
    val activeGoals by viewModel.activeGoals.collectAsState()
    val completedGoals by viewModel.completedGoals.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pyramid_glow")
    val pyramidGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "pyramid_glow"
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddGoal,
                containerColor = Turquoise,
                contentColor = BlackObsidian
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.create_goal),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🏺",
                        style = MaterialTheme.typography.displaySmall,
                        modifier = Modifier.alpha(pyramidGlow)
                    )
                    Text(
                        text = stringResource(R.string.goals),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                }
                Text(
                    text = "Build your pyramids of wealth",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Active Goals
            if (activeGoals.isEmpty()) {
                item {
                    EgyptianCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Text(
                                text = "⛰️",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "No active goals",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = "Start building your pyramid by creating a goal",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(activeGoals) { goal ->
                    GoalCard(
                        goal = goal,
                        currency = currency,
                        onClick = { onEditGoal(goal) }
                    )
                }
            }

            // Completed Goals
            if (completedGoals.isNotEmpty()) {
                item {
                    Text(
                        text = "✨ Completed Pyramids",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGlow,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                items(completedGoals) { goal ->
                    GoalCard(
                        goal = goal,
                        currency = currency,
                        onClick = { onEditGoal(goal) }
                    )
                }
            }
        }
    }
}

