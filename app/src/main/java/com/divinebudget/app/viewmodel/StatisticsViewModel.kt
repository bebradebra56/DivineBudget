package com.divinebudget.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.divinebudget.app.data.entity.TransactionCategory
import com.divinebudget.app.data.entity.TransactionType
import com.divinebudget.app.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import java.util.concurrent.TimeUnit

enum class StatisticsPeriod {
    WEEK, MONTH, YEAR
}

enum class StatisticsType {
    EXPENSE, INCOME
}

data class CategoryStatistic(
    val category: TransactionCategory,
    val amount: Double,
    val percentage: Float
)

class StatisticsViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(StatisticsPeriod.MONTH)
    val selectedPeriod: StateFlow<StatisticsPeriod> = _selectedPeriod.asStateFlow()

    private val _selectedType = MutableStateFlow(StatisticsType.EXPENSE)
    val selectedType: StateFlow<StatisticsType> = _selectedType.asStateFlow()

    private val dateRange = _selectedPeriod.map { period ->
        val endDate = Date()
        val startDate = when (period) {
            StatisticsPeriod.WEEK -> Date(endDate.time - TimeUnit.DAYS.toMillis(7))
            StatisticsPeriod.MONTH -> Date(endDate.time - TimeUnit.DAYS.toMillis(30))
            StatisticsPeriod.YEAR -> Date(endDate.time - TimeUnit.DAYS.toMillis(365))
        }
        Pair(startDate, endDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(Date(), Date()))

    @OptIn(ExperimentalCoroutinesApi::class)
    val periodTransactions = dateRange.flatMapLatest { (startDate, endDate) ->
        repository.getTransactionsByDateRange(startDate, endDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactionsByCategory = combine(periodTransactions, _selectedType) { transactions, type ->
        val filteredTransactions = transactions.filter { 
            it.type == if (type == StatisticsType.EXPENSE) TransactionType.EXPENSE else TransactionType.INCOME
        }
        val total = filteredTransactions.sumOf { it.amount }
        
        if (filteredTransactions.isEmpty()) {
            emptyList()
        } else {
            val categoryTotals = filteredTransactions.groupBy { it.category }
                .mapValues { (_, categoryTransactions) -> 
                    categoryTransactions.sumOf { it.amount }
                }
            
            categoryTotals.map { (category, amount) ->
                CategoryStatistic(
                    category = category,
                    amount = amount,
                    percentage = if (total > 0) ((amount / total) * 100).toFloat() else 0f
                )
            }.sortedByDescending { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome = periodTransactions.map { transactions ->
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense = periodTransactions.map { transactions ->
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setPeriod(period: StatisticsPeriod) {
        _selectedPeriod.value = period
    }

    fun setType(type: StatisticsType) {
        _selectedType.value = type
    }
}

class StatisticsViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

