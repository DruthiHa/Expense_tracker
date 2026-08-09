package com.finaudit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Transaction
import com.finaudit.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // "All", "DEBIT", "CREDIT"
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    
    val filteredList = transactions.filter {
        val matchesSearch = it.merchantName.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Debit" -> it.direction == "DEBIT"
            "Credit" -> it.direction == "CREDIT"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Transactions", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by merchant...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        // Filters
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("All", "Debit", "Credit").forEach { tab ->
                val selected = selectedFilter == tab
                FilterChip(
                    selected = selected,
                    onClick = { selectedFilter = tab },
                    label = { Text(tab) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Transaction List
        LazyColumn(
            modifier = Modifier.weight(1.0f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No matching transactions found.", color = Color.Gray)
                    }
                }
            } else {
                items(filteredList) { tx ->
                    TransactionRow(
                        tx = tx,
                        onEditClick = { editingTransaction = tx },
                        onDeleteClick = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Edit Category Dialog
    editingTransaction?.let { tx ->
        var showCategoryMenu by remember { mutableStateOf(false) }
        val categories = listOf("Food and Dining", "Transport", "Shopping", "Entertainment", "Subscriptions", "Housing", "Health", "Education", "Utilities", "Other")
        
        AlertDialog(
            onDismissRequest = { editingTransaction = null },
            title = { Text("Edit Category for ${tx.merchantName}") },
            text = {
                Column {
                    Text("Current Category: ${tx.category}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { showCategoryMenu = true }) {
                        Text("Select New Category")
                    }
                    DropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    viewModel.updateTransactionCategory(tx, cat)
                                    showCategoryMenu = false
                                    editingTransaction = null
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { editingTransaction = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TransactionRow(
    tx: Transaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formatter = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(tx.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (tx.category) {
                    "Food and Dining" -> "🍔"
                    "Transport" -> "🚗"
                    "Subscriptions" -> "🎬"
                    "Shopping" -> "🛍️"
                    "Entertainment" -> "🍿"
                    "Utilities" -> "⚡"
                    "Education" -> "📚"
                    "Housing" -> "🏠"
                    "Health" -> "💊"
                    else -> "💰"
                },
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1.0f)) {
            Text(tx.merchantName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(tx.category, fontSize = 12.sp, color = Color.Gray)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text("${(tx.categoryConfidence * 100).toInt()}% conf", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
            Text(dateString, fontSize = 11.sp, color = Color.LightGray)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${if (tx.direction == "DEBIT") "-" else "+"}₹${String.format("%,.2f", tx.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (tx.direction == "DEBIT") MaterialTheme.colorScheme.onSurface else Color(0xFF4CAF50)
            )
            Row {
                IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
