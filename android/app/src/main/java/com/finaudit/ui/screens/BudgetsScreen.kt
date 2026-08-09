package com.finaudit.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Budget
import com.finaudit.domain.model.SavingsGoal
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun BudgetsScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val goals by viewModel.goals.collectAsState()

    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showSetBudgetDialog by remember { mutableStateOf(false) }

    val categories = listOf("Food and Dining", "Transport", "Shopping", "Entertainment", "Subscriptions", "Housing", "Health", "Education", "Utilities", "Other")

    // Calculate budget health score
    val overallHealthScore = remember(transactions, budgets) {
        val totalDebits = transactions.filter { it.direction == "DEBIT" }
        val categorySpends = totalDebits.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val checkedCategories = budgets.filter { it.monthlyLimit > 0 }
        if (checkedCategories.isEmpty()) 100.0
        else {
            val valid = checkedCategories.count { b ->
                val spent = categorySpends[b.category] ?: 0.0
                spent <= b.monthlyLimit
            }
            (valid.toDouble() / checkedCategories.size.toDouble()) * 100.0
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Budgets & Goals", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Button(onClick = { showSetBudgetDialog = true }) {
                    Text("Set Budget")
                }
            }
        }

        // Budget Health Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Overall Budget Health Score", fontSize = 13.sp, color = Color.Gray)
                    Text("${overallHealthScore.toInt()}%", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Percentage of categories kept within safe limits.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // Category Budgets Header
        item {
            Text("Category Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Budgets progress trackers
        if (budgets.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No budgets defined for the current month.", color = Color.Gray)
                }
            }
        } else {
            items(budgets) { budget ->
                val spent = transactions.filter { it.direction == "DEBIT" && it.category == budget.category }.sumOf { it.amount }
                val ratio = (spent / budget.monthlyLimit).toFloat().coerceIn(0f, 1f)
                val statusColor = when {
                    ratio >= 1.0f -> Color(0xFFF44336) // Red
                    ratio >= 0.8f -> Color(0xFFFFB74D) // Amber
                    else -> Color(0xFF4CAF50) // Green
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(budget.category, fontWeight = FontWeight.Bold)
                            Text("₹${String.format("%.0f", spent)} / ₹${String.format("%.0f", budget.monthlyLimit)}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = ratio,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = statusColor,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }

        // Savings Goals Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Savings Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { showAddGoalDialog = true }) {
                    Text("Add Goal")
                }
            }
        }

        // Goals list
        if (goals.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No active savings goals.", color = Color.Gray)
                }
            }
        } else {
            items(goals) { goal ->
                GoalRow(
                    goal = goal,
                    onAddContribution = { viewModel.contributeToGoal(goal, 1000.0) }, // One tap add 1k
                    onDelete = { viewModel.deleteGoal(goal) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Set Budget Dialog
    if (showSetBudgetDialog) {
        var selectedCategory by remember { mutableStateOf(categories.first()) }
        var limit by remember { mutableStateOf("") }
        var menuExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSetBudgetDialog = false },
            title = { Text("Set Monthly Budget") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { menuExpanded = true }) {
                        Text("Category: $selectedCategory")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat) }, onClick = {
                                selectedCategory = cat
                                menuExpanded = false
                            })
                        }
                    }
                    OutlinedTextField(value = limit, onValueChange = { limit = it }, label = { Text("Monthly Limit (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val limitVal = limit.toDoubleOrNull() ?: 0.0
                    if (limitVal > 0.0) {
                        viewModel.setCategoryBudget(selectedCategory, limitVal)
                    }
                    showSetBudgetDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSetBudgetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Goal Dialog
    if (showAddGoalDialog) {
        var name by remember { mutableStateOf("") }
        var emoji by remember { mutableStateOf("🎯") }
        var target by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            title = { Text("Add Savings Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Goal Name") })
                    OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("Emoji") })
                    OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Target Amount (₹)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val targetVal = target.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && targetVal > 0.0) {
                        viewModel.addSavingsGoal(name, emoji, targetVal)
                    }
                    showAddGoalDialog = false
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun GoalRow(
    goal: SavingsGoal,
    onAddContribution: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(goal.emoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(goal.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(onClick = onAddContribution) {
                    Text("+ ₹1K")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Saved: ₹${String.format("%.0f", goal.savedAmount)}", fontSize = 12.sp, color = Color.Gray)
                Text("Target: ₹${String.format("%.0f", goal.targetAmount)}", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
