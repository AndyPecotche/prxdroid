// com.example.prxdroid.storage.DataStoreManager.kt
package com.example.prxdroid.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("proxmox_prefs")

class DataStoreManager(private val context: Context) {
    private val baseUrlKey = stringPreferencesKey("base_url")
    private val tokenKey = stringPreferencesKey("token")
    private val nodeKey = stringPreferencesKey("node")
    private val vmidKey = stringPreferencesKey("vmid")

    suspend fun saveCredentials(baseUrl: String, token: String, node: String, vmid: String) {
        context.dataStore.edit {
            it[baseUrlKey] = baseUrl
            it[tokenKey] = token
            it[nodeKey] = node
            it[vmidKey] = vmid
        }
    }

    suspend fun loadCredentials(): ProxmoxCredentials? {
        val prefs = context.dataStore.data.first()
        val baseUrl = prefs[baseUrlKey] ?: return null
        val token = prefs[tokenKey] ?: return null
        val node = prefs[nodeKey] ?: return null
        val vmid = prefs[vmidKey] ?: return null
        return ProxmoxCredentials(baseUrl, token, node, vmid)
    }

    suspend fun clearCredentials() {
        context.dataStore.edit { it.clear() }
    }
}

data class ProxmoxCredentials(
    val baseUrl: String,
    val token: String,
    val node: String,
    val vmid: String
)
