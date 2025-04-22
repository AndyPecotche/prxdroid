// com.example.prxdroid.ui.navigation.NavGraph.kt
package com.example.prxdroid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.prxdroid.ui.screens.HomeScreen
import com.example.prxdroid.ui.screens.CredentialInputScreen
import com.example.prxdroid.viewmodels.ProxmoxViewModel

// AppNavHost.kt
@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: ProxmoxViewModel
) {
    // Escuchar logoutEvent y navegar si se dispara
    LaunchedEffect(Unit) {
        viewModel.logoutEvent.collect {
            navController.popBackStack("credentials", inclusive = false)
        }
    }

    NavHost(navController = navController, startDestination = "credentials") {
        composable("credentials") {
            CredentialInputScreen(
                viewModel = viewModel,
                onContinue = {
                    navController.navigate("control")
                }
            )
        }

        composable("control") {
            HomeScreen(
                viewModel = viewModel,
                onLogout = {
                    viewModel.onLogout()
                }
            )
        }
    }
}