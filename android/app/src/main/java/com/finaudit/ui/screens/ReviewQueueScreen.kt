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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Transaction
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun ReviewQueueScreen(viewModel: MainViewModel) {
    val pendingTransactions by viewModel.reviewQueue.collectAsState()
    val haptic = LocalHapticFeedback.current

    var selectedTransactionForEdit by remember { mutableStateOf<Transaction?>(null) }
    val categories = listOf("Food and Dining", "Transport", "Shopping", "Entertainment", "Subscriptions", "Housing", "Health", "Education", "Utilities", "Other")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Review Queue",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Verify low confidence transaction parses and help FinAudit learn.",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pendingTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🎉 All Caught Up!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("No transactions are pending review right now.", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pendingTransactions) { tx ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                            Color.Transparent
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.merchantName, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                    Text("Source: ${tx.sourceType} • Last 4: ${tx.accountLast4}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text(
                                    text = "₹${String.format("%,.2f", tx.amount)}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (tx.direction == "DEBIT") MaterialTheme.colorScheme.onSurface else Color(0xFF4CAF50)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Suggested category: ", fontSize = 13.sp, color = Color.Gray)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(tx.category, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Text(
                                    text = "Confidence: ${(tx.categoryConfidence * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        // Provide Haptic click
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.confirmTransaction(tx)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Confirm")
                                }

                                OutlinedButton(
                                    onClick = {
                                        selectedTransactionForEdit = tx
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Change Cat")
                                }

                                TextButton(
                                    onClick = {
                                        viewModel.deleteTransaction(tx)
                                    }
                                ) {
                                    Text("Ignore", color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedTransactionForEdit?.let { tx ->
        var expanded by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { selectedTransactionForEdit = null },
            title = { Text("Correct Category") },
            text = {
                LazyColumn(modifier = Modifier.height(250.dp)) {
                    items(categories) { cat ->
                        Text(
                            text = cat,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.updateTransactionCategory(tx, cat)
                                    selectedTransactionForEdit = null
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            fontSize = 16.sp
                        )
                        Divider()
                    }
                }
            },
            confirmButton = {}
        )
    }
}
