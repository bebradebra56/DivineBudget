package com.divinebudget.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.data.entity.Transaction
import com.divinebudget.app.data.entity.TransactionType
import com.divinebudget.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    currency: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    EgyptianCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon with background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.type == TransactionType.INCOME)
                                IncomeGreen.copy(alpha = 0.2f)
                            else
                                ExpenseRed.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (transaction.type == TransactionType.INCOME)
                            Icons.Default.ArrowDownward
                        else
                            Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (transaction.type == TransactionType.INCOME)
                            IncomeGreen
                        else
                            ExpenseRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = transaction.category.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    if (transaction.comment.isNotEmpty()) {
                        Text(
                            text = transaction.comment,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // Amount
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}$currency${
                    String.format("%.2f", transaction.amount)
                }",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (transaction.type == TransactionType.INCOME)
                    IncomeGreen
                else
                    ExpenseRed
            )
        }
    }
}

