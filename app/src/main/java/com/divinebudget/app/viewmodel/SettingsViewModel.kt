package com.divinebudget.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.divinebudget.app.data.preferences.UserPreferences
import com.divinebudget.app.data.repository.GoalRepository
import com.divinebudget.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferences: UserPreferences,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModel() {

    val currency = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "$")

    val remindersEnabled = userPreferences.remindersEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val theme = userPreferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "light")

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            userPreferences.setCurrency(currency)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setRemindersEnabled(enabled)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            userPreferences.setTheme(theme)
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
            goalRepository.deleteAllGoals()
        }
    }
}

class SettingsViewModelFactory(
    private val userPreferences: UserPreferences,
    private val transactionRepository: TransactionRepository,
    private val goalRepository: GoalRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userPreferences, transactionRepository, goalRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

