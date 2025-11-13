package com.divinebudget.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.divinebudget.app.data.entity.Transaction
import com.divinebudget.app.data.entity.TransactionCategory
import com.divinebudget.app.data.entity.TransactionType
import com.divinebudget.app.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    val allTransactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _filterType = MutableStateFlow<TransactionType?>(null)
    val filterType: StateFlow<TransactionType?> = _filterType.asStateFlow()

    val filteredTransactions = combine(allTransactions, _filterType) { transactions, type ->
        if (type != null) {
            transactions.filter { it.type == type }
        } else {
            transactions
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(type: TransactionType?) {
        _filterType.value = type
    }

    fun addTransaction(
        type: TransactionType,
        category: TransactionCategory,
        amount: Double,
        date: Date,
        comment: String = ""
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    type = type,
                    category = category,
                    amount = amount,
                    date = date,
                    comment = comment
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

