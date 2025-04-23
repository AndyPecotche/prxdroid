package com.prxdroid2.prxdroid2.ui.screens



import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prxdroid2.prxdroid2.viewmodels.ProxmoxViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
@Composable


fun CredentialInputScreen(
    viewModel: ProxmoxViewModel,
    onContinue: () -> Unit
) {
    var baseUrl by remember { mutableStateOf("") }
    var token by remember { mutableStateOf("") }
    var node by remember { mutableStateOf("") }
    var vmid by remember { mutableStateOf("") }

    val context = LocalContext.current
    val darkColorScheme = darkColorScheme(
        primary = Color(0xFFB0BEC5),
        secondary = Color(0xFFCFD8DC),
        tertiary = Color(0xFF78909C),
        surface = Color(0xFF121212),
        onSurface = Color(0xFFE0E0E0),
        background = Color(0xFF1E1E1E)
    )

    LaunchedEffect(Unit) {
        val creds = viewModel.dataStore.loadCredentials()
        creds?.let {
            baseUrl = it.baseUrl
            token = it.token
            node = it.node
            vmid = it.vmid
        }
    }

    MaterialTheme(colorScheme = darkColorScheme) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Configuración Proxmox",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Base URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("Token") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = node,
                    onValueChange = { node = it },
                    label = { Text("Node") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = MaterialTheme.shapes.medium
                )

                OutlinedTextField(
                    value = vmid,
                    onValueChange = { vmid = it },
                    label = { Text("VMID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    shape = MaterialTheme.shapes.medium
                )

                Button(
                    onClick = {
                        viewModel.baseUrl = baseUrl
                        viewModel.token = token
                        viewModel.node = node
                        viewModel.vmid = vmid
                        viewModel.viewModelScope.launch {
                            viewModel.saveCredentials()
                            onContinue()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Continuar", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedLabelColor = MaterialTheme.colorScheme.onSurface,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)