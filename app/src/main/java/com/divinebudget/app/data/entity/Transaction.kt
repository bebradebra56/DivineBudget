package com.divinebudget.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val category: TransactionCategory,
    val amount: Double,
    val date: Date,
    val comment: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}

enum class TransactionCategory(val displayName: String, val icon: String) {
    // Income categories
    SALARY("Salary", "💰"),
    BUSINESS("Business", "📈"),
    GIFT("Gift", "🎁"),
    OTHER_INCOME("Other Income", "💵"),
    
    // Expense categories
    FOOD("Food", "🌾"),
    HOME("Home", "🏠"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🎁"),
    ENTERTAINMENT("Entertainment", "🎭"),
    HEALTH("Health", "⚕️"),
    EDUCATION("Education", "📚"),
    OTHER_EXPENSE("Other Expense", "💸")
}

