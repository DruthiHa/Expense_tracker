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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.finaudit.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(viewModel: MainViewModel, onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    
    val whitelisted by viewModel.whitelistedSenders.collectAsState()
    val blacklisted by viewModel.blacklistedSenders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step header indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(4) { idx ->
                val active = idx + 1 <= step
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (active) MaterialTheme.colorScheme.primary else Color.DarkGray)
                )
            }
        }

        // Mid section based on active step
        Box(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when (step) {
                1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FinAudit 🛡️", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("AI-Powered Personal Finance & Subscription Audit", fontSize = 16.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("FinAudit scans transaction SMS alerts and payment push notifications to audit your money automatically — with 0% data ever leaving the device.", color = Color.Gray, textAlign = TextAlign.Center)
                }
                
                2 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Permissions & Privacy Setup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("We require the following permissions to capture transactions automatically:\n\n1. SMS Permissions: To parse incoming banking notifications.\n2. Notification Listener: To read Google Pay / PhonePe transaction push messages.", color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Text("🔒 Privacy Guarantee: No raw messages or details ever exit your phone. All parsing & AI runs locally offline.", modifier = Modifier.padding(12.dp), fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    }
                }

                3 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SMS Whitelisting", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Check the banking keywords we will scan. You can customize this list at any time:", color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    val items = listOf("HDFCBK", "ICICIB", "SBIINB", "AXISBK", "KOTAKB", "YESBNK")
                    LazyColumn(modifier = Modifier.height(180.dp)) {
                        items(items) { item ->
                            val isChecked = whitelisted.contains(item)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newSet = whitelisted.toMutableSet()
                                        if (isChecked) newSet.remove(item) else newSet.add(item)
                                        viewModel.whitelistedSenders.value = newSet
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item)
                                Checkbox(checked = isChecked, onCheckedChange = null)
                            }
                        }
                    }
                }

                4 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("All Set!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Imports & Scanning is now configured. Ready to run your initial scan of the last 30 days of SMS history to pre-populate dashboards?", textAlign = TextAlign.Center, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("FinAudit Engine Ready", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Navigation bottom controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (step > 1) {
                TextButton(onClick = { step-- }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(60.dp))
            }

            Button(
                onClick = {
                    if (step < 4) {
                        step++
                    } else {
                        viewModel.isOnboarded.value = true
                        onFinish()
                    }
                }
            ) {
                Text(if (step == 4) "Finish Setup" else "Continue")
            }
        }
    }
}
