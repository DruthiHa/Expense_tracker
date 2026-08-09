package com.finaudit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Subscription
import com.finaudit.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(viewModel: MainViewModel) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    val selectedSubs = remember { mutableStateListOf<Subscription>() }

    val annualWaste = subscriptions.filter { it.status == "UNUSED" }.sumOf { it.amount * 12 }
    val annualSavingOpportunity = selectedSubs.sumOf { it.amount * 12 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Subscriptions", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add subscription manually")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Subscription Summary / Saving Opportunities Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Annual Unused Waste", fontSize = 13.sp, color = Color.Gray)
                Text("₹${String.format("%,.2f", annualWaste)}", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFF44336))
                
                if (selectedSubs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Selected for cancel saving: ₹${String.format("%,.0f", annualSavingOpportunity)}/yr", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1.0f)
        ) {
            if (subscriptions.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("No subscriptions registered yet.", color = Color.Gray)
                    }
                }
            } else {
                items(subscriptions) { sub ->
                    val isSelected = selectedSubs.contains(sub)
                    SubscriptionRow(
                        sub = sub,
                        isSelected = isSelected,
                        onSelectToggle = {
                            if (isSelected) selectedSubs.remove(sub) else selectedSubs.add(sub)
                        },
                        onStatusClick = { viewModel.cycleSubscriptionStatus(sub) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var amount by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Subscriptions") }
        
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Subscription") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Service Name") })
                    OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount/mo") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && amt > 0.0) {
                            viewModel.addSubscriptionManually(name, amt, category)
                        }
                        showAddDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SubscriptionRow(
    sub: Subscription,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onStatusClick: () -> Unit
) {
    val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    val renewDateStr = formatter.format(Date(sub.renewDate))

    val statusColor = when (sub.status) {
        "ACTIVE" -> Color(0xFF4CAF50)
        "UNDER_REVIEW" -> Color(0xFFFFB74D)
        else -> Color(0xFFF44336)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
            .clickable { onSelectToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(sub.emoji, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1.0f)) {
            Text(sub.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Renew date: $renewDateStr", fontSize = 12.sp, color = Color.Gray)
            Text("Annual cost: ₹${String.format("%,.0f", sub.amount * 12)}", fontSize = 11.sp, color = Color.LightGray)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("₹${String.format("%,.0f", sub.amount)}/mo", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .clickable { onStatusClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(sub.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
