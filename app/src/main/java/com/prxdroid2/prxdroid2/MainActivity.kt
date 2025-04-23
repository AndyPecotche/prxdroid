package com.prxdroid2.prxdroid2


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.prxdroid2.prxdroid2.ui.navigation.AppNavHost
import com.prxdroid2.prxdroid2.viewmodels.ProxmoxViewModel

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
