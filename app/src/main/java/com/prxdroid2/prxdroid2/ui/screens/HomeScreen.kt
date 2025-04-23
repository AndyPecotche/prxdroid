package com.prxdroid2.prxdroid2.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prxdroid2.prxdroid2.viewmodels.ProxmoxViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme




@Composable
fun HomeScreen(
    viewModel: ProxmoxViewModel,
    onLogout: () -> Unit
) {
    val estado by viewModel.status.collectAsState()
    val vmName by viewModel.vmName.collectAsState()
    val loading by viewModel.loading.collectAsState()

    val darkColorScheme = darkColorScheme(
        primary = Color(0xFFB0BEC5),
        secondary = Color(0xFFCFD8DC),
        tertiary = Color(0xFF78909C),
        surface = Color(0xFF121212),
        onSurface = Color(0xFFE0E0E0),
        background = Color(0xFF1E1E1E),
        error = Color(0xFFCF6679)
    )

    LaunchedEffect(Unit) {
        viewModel.fetchInitialData()
    }

    MaterialTheme(colorScheme = darkColorScheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when {
                loading -> LoadingIndicator()
                else -> MainContent(estado, vmName, viewModel, onLogout)
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

@Composable
private fun MainContent(
    estado: String,
    vmName: String,
    viewModel: ProxmoxViewModel,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VMInfoSection(vmName, estado)
            ActionButtons(viewModel)
            LogoutButton(onLogout)
        }
    }
}

@Composable
private fun VMInfoSection(vmName: String, estado: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = vmName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Estado: $estado",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ActionButtons(viewModel: ProxmoxViewModel) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        listOf(
            "Iniciar" to { viewModel.start() },
            "Detener" to { viewModel.stop() },
            "Reiniciar" to { viewModel.reset() },
            "Apagar" to { viewModel.shutdown() },
            "Suspender" to { viewModel.suspendVM() },
            "Reiniciar VM" to { viewModel.reboot() }
        ).forEach { (text, action) ->
            ActionButton(text, action)
        }
    }
}

@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError
        )
    ) {
        Text("Cerrar Sesión", style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text, style = MaterialTheme.typography.bodyLarge)
    }
}