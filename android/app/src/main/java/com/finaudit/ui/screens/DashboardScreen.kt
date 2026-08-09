package com.finaudit.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Transaction
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(viewModel: MainViewModel, onNavigate: (String) -> Unit) {
    val transactions by viewModel.transactions.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()
    
    val totalIncome = transactions.filter { it.direction == "CREDIT" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.direction == "DEBIT" }.sumOf { it.amount }
    val netSavings = totalIncome - totalExpense
    val subCost = subscriptions.filter { it.status == "ACTIVE" }.sumOf { it.amount }

    val savingsRate = if (totalIncome > 0) (netSavings / totalIncome) * 100 else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "FinAudit",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Safe & Automatic Financial Dashboard",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            val context = androidx.compose.ui.platform.LocalContext.current
            Button(
                onClick = {
                    viewModel.simulateNotification(
                        context,
                        "Google Pay: Paid Rs 450.00 to Swiggy using A/c ending 1234 on 07-Aug-2026."
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simulate Google Pay ₹450 (Swiggy)", fontWeight = FontWeight.Bold)
            }
        }

        // Summary Card Grid
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text("Total Net Savings", fontSize = 14.sp, color = Color.Gray)
                    Text("₹${String.format("%,.2f", netSavings)}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Income", fontSize = 12.sp, color = Color.Gray)
                            Text("₹${String.format("%,.0f", totalIncome)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Column {
                            Text("Expenses", fontSize = 12.sp, color = Color.Gray)
                            Text("₹${String.format("%,.0f", totalExpense)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                        }
                        Column {
                            Text("Subs/mo", fontSize = 12.sp, color = Color.Gray)
                            Text("₹${String.format("%,.0f", subCost)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }

        // Savings Rate Indicator
        item {
            val rateColor = when {
                savingsRate >= 20.0 -> Color(0xFF4CAF50)
                savingsRate >= 10.0 -> Color(0xFFFFB74D)
                else -> Color(0xFFE57373)
            }
            
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Monthly Savings Rate", fontSize = 14.sp, color = Color.Gray)
                        Text("${String.format("%.1f", savingsRate)}%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = rateColor)
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(rateColor)
                    )
                }
            }
        }

        // Spending by Category Chart Summary
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top Category Spend", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    val categoryTotals = transactions.filter { it.direction == "DEBIT" }
                        .groupBy { it.category }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }
                        .toList()
                        .sortedByDescending { it.second }
                        .take(3)

                    if (categoryTotals.isEmpty()) {
                        Text("No expenses logged this month.", color = Color.Gray)
                    } else {
                        categoryTotals.forEach { (cat, amt) ->
                            val percent = if (totalExpense > 0) (amt / totalExpense).toFloat() else 0f
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat, fontSize = 14.sp)
                                    Text("₹${String.format("%.0f", amt)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                LinearProgressIndicator(
                                    progress = percent,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Feed Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    "See All",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate("TRANSACTIONS") }
                )
            }
        }

        // Recent 5 Transactions
        val recentTransactions = transactions.take(5)
        if (recentTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions captured yet.", color = Color.Gray)
                }
            }
        } else {
            items(recentTransactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Circle Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
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
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1.0f)) {
                        Text(tx.merchantName, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(tx.category, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text(
                        text = "${if (tx.direction == "DEBIT") "-" else "+"}₹${String.format("%.0f", tx.amount)}",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (tx.direction == "DEBIT") MaterialTheme.colorScheme.onSurface else Color(0xFF4CAF50)
                    )
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
