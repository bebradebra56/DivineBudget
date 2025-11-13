package com.divinebudget.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.divinebudget.app.R
import com.divinebudget.app.data.entity.TransactionType
import com.divinebudget.app.ui.components.EgyptianCard
import com.divinebudget.app.ui.components.TransactionItem
import com.divinebudget.app.ui.theme.*
import com.divinebudget.app.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel,
    currency: String,
    onAddTransaction: () -> Unit
) {
    val filteredTransactions by viewModel.filteredTransactions.collectAsState()
    val filterType by viewModel.filterType.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = Gold,
                contentColor = BlackObsidian
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_transaction),
                    modifier = Modifier.scale(1.3f)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "📜 ${stringResource(R.string.transactions)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Gold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterType == null,
                    onClick = { viewModel.setFilter(null) },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Gold,
                        selectedLabelColor = BlackObsidian
                    )
                )
                FilterChip(
                    selected = filterType == TransactionType.INCOME,
                    onClick = { viewModel.setFilter(TransactionType.INCOME) },
                    label = { Text(stringResource(R.string.income)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen,
                        selectedLabelColor = BlackObsidian
                    )
                )
                FilterChip(
                    selected = filterType == TransactionType.EXPENSE,
                    onClick = { viewModel.setFilter(TransactionType.EXPENSE) },
                    label = { Text(stringResource(R.string.expense)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed,
                        selectedLabelColor = BlackObsidian
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transactions list
            if (filteredTransactions.isEmpty()) {
                EgyptianCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "🏺",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Text(
                            text = "No transactions found",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Start adding transactions to track your finances",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            currency = currency
                        )
                    }
                }
            }
        }
    }
}

