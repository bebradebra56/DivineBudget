package com.divinebudget.app.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Overview : Screen("overview")
    object Transactions : Screen("transactions")
    object Statistics : Screen("statistics")
    object Goals : Screen("goals")
    object Settings : Screen("settings")
    object AddTransaction : Screen("add_transaction")
    object AddGoal : Screen("add_goal")
    object EditGoal : Screen("edit_goal/{goalId}") {
        fun createRoute(goalId: Long) = "edit_goal/$goalId"
    }
}

