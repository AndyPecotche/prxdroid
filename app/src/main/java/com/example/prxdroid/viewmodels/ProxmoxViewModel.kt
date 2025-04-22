package com.example.prxdroid.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prxdroid.storage.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

class ProxmoxViewModel(
    context: Context
) : ViewModel() {

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    val dataStore = DataStoreManager(context)

    // Estados
    private val _status = MutableStateFlow("Cargando estado...")
    val status: StateFlow<String> = _status

    private val _vmName = MutableStateFlow("Cargando nombre...")
    val vmName: StateFlow<String> = _vmName

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    var baseUrl: String = "https://100.67.36.51:8006"
    var token: String = "usuario@pve!altoken=6e783b69-572b-493b-88b4-29a91f35b1c2"
    var node: String = "proxmox-pc"
    var vmid: String = "100"

    init {
        setupSSL()
        viewModelScope.launch {
            loadStoredCredentials()
            fetchInitialData()
        }
    }

    private fun setupSSL() {
        val trustAllCertificates = object : X509TrustManager {
            override fun getAcceptedIssuers() = null
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) {}
        }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(null, arrayOf(trustAllCertificates), java.security.SecureRandom())
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    suspend fun loadStoredCredentials() {
        dataStore.loadCredentials()?.let {
            baseUrl = it.baseUrl
            token = it.token
            node = it.node
            vmid = it.vmid
        }
    }

    suspend fun saveCredentials() {
        dataStore.saveCredentials(baseUrl, token, node, vmid)
    }

    suspend fun clearCredentials() {
        dataStore.clearCredentials()
    }

    fun onLogout() {
        viewModelScope.launch {
            clearCredentials()
            _logoutEvent.emit(Unit)
        }
    }

    fun fetchInitialData() {
        getStatus()
        getVMConfig()
    }

    fun getStatus() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = withContext(Dispatchers.IO) {
                    makeApiCall("status/current")
                }

                _status.value = when {
                    "\"status\":\"running\"" in result -> "Encendido"
                    "\"status\":\"stopped\"" in result -> "Apagado"
                    else -> "Estado desconocido"
                }
            } catch (e: Exception) {
                _status.value = "Error: ${e.localizedMessage}"
                Log.e("ProxmoxAPI", "Error en getStatus", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun getVMConfig() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = withContext(Dispatchers.IO) {
                    makeApiCall("config")
                }

                val nameRegex = "\"name\":\"(.*?)\"".toRegex()
                _vmName.value = nameRegex.find(result)?.groupValues?.get(1) ?: "Nombre no disponible"
            } catch (e: Exception) {
                _vmName.value = "Error al obtener nombre"
                Log.e("ProxmoxAPI", "Error en getVMConfig", e)
            } finally {
                _loading.value = false
            }
        }
    }

    private suspend fun makeApiCall(endpoint: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$baseUrl/api2/json/nodes/$node/qemu/$vmid/$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "GET"
                setRequestProperty("Authorization", "PVEAPIToken=$token")
                connectTimeout = 3000
                readTimeout = 3000
            }
            connection.inputStream.bufferedReader().readText().also {
                Log.d("ProxmoxAPI", "Respuesta de $endpoint: $it")
            }
        }
    }

    fun start() = makeActionRequest("start")
    fun stop() = makeActionRequest("stop")
    fun reset() = makeActionRequest("reset")
    fun shutdown() = makeActionRequest("shutdown")
    fun suspendVM() = makeActionRequest("suspend")
    fun reboot() = makeActionRequest("reboot")

    private fun makeActionRequest(action: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    makeApiCall("status/$action", "POST")
                }
                getStatus()
            } catch (e: Exception) {
                Log.e("ProxmoxAPI", "Error en $action", e)
            }
        }
    }

    private suspend fun makeApiCall(endpoint: String, method: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$baseUrl/api2/json/nodes/$node/qemu/$vmid/$endpoint")
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = method
                setRequestProperty("Authorization", "PVEAPIToken=$token")
                connectTimeout = 3000
                readTimeout = 3000
            }
            connection.inputStream.bufferedReader().readText()
        }
    }
}