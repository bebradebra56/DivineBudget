package com.divinebudget.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.divinebudget.app.ejgpoer.presentation.app.DivineBudgetApplication
import com.divinebudget.app.navigation.Screen
import com.divinebudget.app.ui.components.EgyptianBottomNavigation
import com.divinebudget.app.viewmodel.*
import kotlinx.coroutines.runBlocking

@Composable
fun MainScreen(
    application: DivineBudgetApplication,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Overview.route
    val currency by application.userPreferences.currency.collectAsState(initial = "$")

    // Screens that should show bottom navigation
    val screensWithBottomNav = setOf(
        Screen.Overview.route,
        Screen.Transactions.route,
        Screen.Statistics.route,
        Screen.Goals.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in screensWithBottomNav) {
                EgyptianBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Screen.Overview.route) { 
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Overview.route
            ) {
                composable(Screen.Overview.route) {
                    val viewModel: OverviewViewModel = viewModel(
                        factory = OverviewViewModelFactory(
                            application.transactionRepository,
                            application.goalRepository
                        )
                    )
                    OverviewScreen(
                        viewModel = viewModel,
                        currency = currency,
                        onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                        onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                        onNavigateToGoals = { navController.navigate(Screen.Goals.route) }
                    )
                }

                composable(Screen.Transactions.route) {
                    val viewModel: TransactionViewModel = viewModel(
                        factory = TransactionViewModelFactory(application.transactionRepository)
                    )
                    TransactionsScreen(
                        viewModel = viewModel,
                        currency = currency,
                        onAddTransaction = { navController.navigate(Screen.AddTransaction.route) }
                    )
                }

                composable(Screen.Statistics.route) {
                    val viewModel: StatisticsViewModel = viewModel(
                        factory = StatisticsViewModelFactory(application.transactionRepository)
                    )
                    StatisticsScreen(
                        viewModel = viewModel,
                        currency = currency
                    )
                }

                composable(Screen.Goals.route) {
                    val viewModel: GoalViewModel = viewModel(
                        factory = GoalViewModelFactory(application.goalRepository)
                    )
                    GoalsScreen(
                        viewModel = viewModel,
                        currency = currency,
                        onAddGoal = { navController.navigate(Screen.AddGoal.route) },
                        onEditGoal = { goal ->
                            navController.navigate(Screen.EditGoal.createRoute(goal.id))
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    val viewModel: SettingsViewModel = viewModel(
                        factory = SettingsViewModelFactory(
                            application.userPreferences,
                            application.transactionRepository,
                            application.goalRepository
                        )
                    )
                    SettingsScreen(viewModel = viewModel)
                }

                composable(Screen.AddTransaction.route) {
                    val viewModel: TransactionViewModel = viewModel(
                        factory = TransactionViewModelFactory(application.transactionRepository)
                    )
                    AddTransactionScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.AddGoal.route) {
                    val viewModel: GoalViewModel = viewModel(
                        factory = GoalViewModelFactory(application.goalRepository)
                    )
                    AddGoalScreen(
                        viewModel = viewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.EditGoal.route,
                    arguments = listOf(navArgument("goalId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val goalId = backStackEntry.arguments?.getLong("goalId") ?: return@composable
                    val viewModel: GoalViewModel = viewModel(
                        factory = GoalViewModelFactory(application.goalRepository)
                    )
                    val goal = runBlocking { application.goalRepository.getGoalById(goalId) }
                    
                    if (goal != null) {
                        EditGoalScreen(
                            goal = goal,
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

