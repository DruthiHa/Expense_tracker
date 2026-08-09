package com.finaudit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.finaudit.ui.navigation.FinAuditAppNavigation
import com.finaudit.ui.theme.FinAuditTheme
import com.finaudit.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if this is the first app launch after installation
        val prefs = getSharedPreferences("finaudit_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        if (isFirstLaunch) {
            // Run database reset on background thread to start fresh
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                viewModel.clearAllDatabaseData()
            }
            prefs.edit().putBoolean("is_first_launch", false).apply()
        }

        setContent {
            FinAuditTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FinAuditAppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
