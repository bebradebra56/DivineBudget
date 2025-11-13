package com.divinebudget.app.data.repository

import com.divinebudget.app.data.dao.GoalDao
import com.divinebudget.app.data.entity.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {
    
    fun getAllGoals(): Flow<List<Goal>> = goalDao.getAllGoals()
    
    fun getActiveGoals(): Flow<List<Goal>> = goalDao.getActiveGoals()
    
    fun getCompletedGoals(): Flow<List<Goal>> = goalDao.getCompletedGoals()
    
    suspend fun getGoalById(id: Long): Goal? = goalDao.getGoalById(id)
    
    suspend fun insertGoal(goal: Goal): Long = goalDao.insertGoal(goal)
    
    suspend fun updateGoal(goal: Goal) = goalDao.updateGoal(goal)
    
    suspend fun deleteGoal(goal: Goal) = goalDao.deleteGoal(goal)
    
    suspend fun deleteAllGoals() = goalDao.deleteAllGoals()
}

