package com.divinebudget.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.divinebudget.app.navigation.Screen
import com.divinebudget.app.ui.theme.*

@Composable
fun EgyptianBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = BlackObsidian,
        contentColor = Gold,
        modifier = Modifier
            .height(70.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlackObsidian,
                        StoneBrown.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        BottomNavItem(
            icon = Icons.Default.Home,
            label = "Overview",
            selected = currentRoute == Screen.Overview.route,
            onClick = { onNavigate(Screen.Overview.route) }
        )
        
        BottomNavItem(
            icon = Icons.Default.List,
            label = "Transactions",
            selected = currentRoute == Screen.Transactions.route,
            onClick = { onNavigate(Screen.Transactions.route) }
        )
        
        BottomNavItem(
            icon = Icons.Default.BarChart,
            label = "Statistics",
            selected = currentRoute == Screen.Statistics.route,
            onClick = { onNavigate(Screen.Statistics.route) }
        )
        
        BottomNavItem(
            icon = Icons.Default.Flag,
            label = "Goals",
            selected = currentRoute == Screen.Goals.route,
            onClick = { onNavigate(Screen.Goals.route) }
        )
        
        BottomNavItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            selected = currentRoute == Screen.Settings.route,
            onClick = { onNavigate(Screen.Settings.route) }
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "nav_item_scale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = "nav_item_glow"
    )

    NavigationBarItem(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.scale(scale)
            ) {
                // Glow effect for selected item
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Turquoise.copy(alpha = 0.3f * glowAlpha),
                                        Turquoise.copy(alpha = 0f)
                                    )
                                )
                            )
                    )
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(32.dp),
                    tint = if (selected) Gold else LightSand.copy(alpha = 0.6f)
                )
            }
        },
        label = null,
        selected = selected,
        onClick = onClick,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Gold,
            selectedTextColor = Gold,
            unselectedIconColor = LightSand.copy(alpha = 0.6f),
            unselectedTextColor = LightSand.copy(alpha = 0.6f),
            indicatorColor = Turquoise.copy(alpha = 0.2f)
        )
    )
}

