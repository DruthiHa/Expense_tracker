package com.finaudit.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.domain.model.Alert
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun SmartAlertsScreen(viewModel: MainViewModel, onNavigate: (String) -> Unit) {
    val alerts by viewModel.alerts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text("Smart Alerts", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Real-time AI alerts based on transaction captures & budget limits", fontSize = 13.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1.0f)
        ) {
            if (alerts.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("No active smart alerts.", color = Color.Gray)
                    }
                }
            } else {
                items(alerts) { alert ->
                    AlertRow(
                        alert = alert,
                        onActionClick = { onNavigate(alert.actionScreen) },
                        onDismissClick = { viewModel.dismissAlert(alert) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun AlertRow(
    alert: Alert,
    onActionClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val (color, emoji) = when (alert.type) {
        "CRITICAL" -> Color(0xFFF44336) to "🚨"
        "WARNING" -> Color(0xFFFFB74D) to "⚠️"
        else -> Color(0xFF4CAF50) to "🎉"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1.0f)) {
                Text(
                    text = alert.type,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = alert.message,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Take Action",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { onActionClick() }
                    )
                    Text(
                        text = "Dismiss",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { onDismissClick() }
                    )
                }
            }
        }
    }
}
