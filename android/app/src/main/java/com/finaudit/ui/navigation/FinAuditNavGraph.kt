package com.finaudit.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.finaudit.ui.screens.*
import com.finaudit.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Home)
    object Transactions : Screen("transactions", "Transactions", Icons.Default.List)
    object Subscriptions : Screen("subscriptions", "Subscriptions", Icons.Default.ShoppingCart)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Info)
    object Budgets : Screen("budgets", "Budgets", Icons.Default.DateRange)
    object ReviewQueue : Screen("review", "Review", Icons.Default.PlayArrow)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Warning)
}

@Composable
fun FinAuditAppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val isOnboarded by viewModel.isOnboarded.collectAsState()
    val reviewItems by viewModel.reviewQueue.collectAsState()

    if (!isOnboarded) {
        OnboardingScreen(viewModel = viewModel, onFinish = {
            // Direct state trigger switches screen state layout without backstack crash
        })
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    val items = listOf(
                        Screen.Dashboard,
                        Screen.Transactions,
                        Screen.Subscriptions,
                        Screen.Analytics,
                        Screen.Budgets,
                        Screen.ReviewQueue,
                        Screen.Alerts
                    )
                    
                    items.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(screen.title, maxLines = 1) },
                            icon = {
                                if (screen == Screen.ReviewQueue && reviewItems.isNotEmpty()) {
                                    BadgedBox(badge = { Badge { Text("${reviewItems.size}") } }) {
                                        Icon(screen.icon, contentDescription = screen.title)
                                    }
                                } else {
                                    Icon(screen.icon, contentDescription = screen.title)
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(viewModel = viewModel, onNavigate = { route ->
                        navController.navigate(route.lowercase())
                    })
                }
                composable(Screen.Transactions.route) {
                    TransactionsScreen(viewModel = viewModel)
                }
                composable(Screen.Subscriptions.route) {
                    SubscriptionsScreen(viewModel = viewModel)
                }
                composable(Screen.Analytics.route) {
                    AnalyticsScreen(viewModel = viewModel)
                }
                composable(Screen.Budgets.route) {
                    BudgetsScreen(viewModel = viewModel)
                }
                composable(Screen.ReviewQueue.route) {
                    ReviewQueueScreen(viewModel = viewModel)
                }
                composable(Screen.Alerts.route) {
                    SmartAlertsScreen(viewModel = viewModel, onNavigate = { route ->
                        navController.navigate(route.lowercase())
                    })
                }
            }
        }
    }
}
