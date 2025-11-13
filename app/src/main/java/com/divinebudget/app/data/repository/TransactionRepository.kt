package com.divinebudget.app.data.repository

import com.divinebudget.app.data.dao.TransactionDao
import com.divinebudget.app.data.entity.Transaction
import com.divinebudget.app.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()
    
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type)
    
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(startDate, endDate)
    
    fun getTotalByType(type: TransactionType): Flow<Double?> =
        transactionDao.getTotalByType(type)
    
    suspend fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Date,
        endDate: Date
    ): Double = transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate) ?: 0.0
    
    suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)
    
    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)
    
    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.updateTransaction(transaction)
    
    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)
    
    suspend fun deleteAllTransactions() =
        transactionDao.deleteAllTransactions()
}

