// com.example.prxdroid.MainActivity.kt
package com.example.prxdroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.prxdroid.ui.navigation.AppNavHost
import com.example.prxdroid.viewmodels.ProxmoxViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val proxmoxViewModel = ProxmoxViewModel(applicationContext)

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController, viewModel = proxmoxViewModel)
        }
    }
}
