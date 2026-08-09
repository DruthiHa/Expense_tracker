package com.finaudit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun AnalyticsScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val subscriptions by viewModel.subscriptions.collectAsState()

    val totalExpenses = transactions.filter { it.direction == "DEBIT" }.sumOf { it.amount }

    // Spending Share categories
    val categoryShares = transactions.filter { it.direction == "DEBIT" }
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    // Top 5 Merchants
    val topMerchants = transactions.filter { it.direction == "DEBIT" }
        .groupBy { it.merchantName }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }
        .take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Analytics", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Detailed spending breakdown and visual audit analytics", fontSize = 13.sp, color = Color.Gray)
        }

        // Subscriptions Audit Box
        item {
            val totalCount = subscriptions.size
            val activeCount = subscriptions.count { it.status == "ACTIVE" }
            val reviewCount = subscriptions.count { it.status == "UNDER_REVIEW" }
            val unusedCount = subscriptions.count { it.status == "UNUSED" }
            val monthlyWaste = subscriptions.filter { it.status == "UNUSED" }.sumOf { it.amount }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Subscription Audit Panel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Active", fontSize = 12.sp, color = Color.Gray)
                            Text("$activeCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Review", fontSize = 12.sp, color = Color.Gray)
                            Text("$reviewCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFB74D))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Unused", fontSize = 12.sp, color = Color.Gray)
                            Text("$unusedCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                        }
                    }
                    
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Monthly Waste Potential", color = Color.Gray)
                        Text("₹${String.format("%,.0f", monthlyWaste)}", fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                    }
                }
            }
        }

        // Donut replacement: Category Spend Share list
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Spending Share by Category", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (categoryShares.isEmpty()) {
                        Text("No transaction logs to audit", color = Color.Gray)
                    } else {
                        categoryShares.forEach { (cat, amt) ->
                            val pct = if (totalExpenses > 0) (amt / totalExpenses) * 100 else 0.0
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat, fontSize = 14.sp)
                                    Text("${String.format("%.1f", pct)}% (₹${String.format("%.0f", amt)})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                LinearProgressIndicator(
                                    progress = (pct / 100).toFloat(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top 5 Merchants Bar chart simulation
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Top 5 Merchants by Spend", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (topMerchants.isEmpty()) {
                        Text("No merchant spends detected.", color = Color.Gray)
                    } else {
                        val maxMerchantSpend = topMerchants.maxOfOrNull { it.second } ?: 1.0
                        topMerchants.forEach { (merchant, amt) ->
                            val relativeRatio = (amt / maxMerchantSpend).toFloat()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(merchant, modifier = Modifier.width(100.dp), fontSize = 13.sp, maxLines = 1)
                                Box(
                                    modifier = Modifier
                                        .weight(1.0f)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(relativeRatio)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("₹${String.format("%.0f", amt)}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
