package com.divinebudget.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.divinebudget.app.data.entity.Goal
import com.divinebudget.app.data.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class GoalViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    val activeGoals = repository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals = repository.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createGoal(
        name: String,
        targetAmount: Double,
        dailyContribution: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.insertGoal(
                Goal(
                    name = name,
                    targetAmount = targetAmount,
                    dailyContribution = dailyContribution,
                    createdAt = Date()
                )
            )
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun addToGoal(goal: Goal, amount: Double) {
        viewModelScope.launch {
            val newAmount = goal.currentAmount + amount
            val isCompleted = newAmount >= goal.targetAmount
            repository.updateGoal(
                goal.copy(
                    currentAmount = newAmount,
                    isCompleted = isCompleted,
                    completedAt = if (isCompleted && goal.completedAt == null) Date() else goal.completedAt
                )
            )
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }
}

class GoalViewModelFactory(
    private val repository: GoalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoalViewModel::class.java)) {
            return GoalViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

