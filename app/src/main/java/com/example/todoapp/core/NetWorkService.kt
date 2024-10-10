package com.example.todoapp.core

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.todoapp.R
import com.example.todoapp.utils.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetWorkService @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private fun showToast() {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, R.string.internet_error, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Verifica el estado de la red. Si [showErrorToast] es true, mostrará un Toast en caso de
     * que no haya conexión.
     */
    fun getNetworkService(showErrorToast: Boolean = true): Boolean {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            if (network == null) {
                Logger.error("NetworkUtils", "No active network")
                if (showErrorToast) showToast()
                return false
            }

            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities == null) {
                Logger.error("NetworkUtils", "No network capabilities available")
                if (showErrorToast) showToast()
                return false
            }

            // Verificar si la red tiene conectividad a internet
            val hasInternet =
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

            if (!hasInternet) {
                Logger.error("NetworkUtils", "No internet transport available")
                if (showErrorToast) showToast()
            }

            hasInternet
        } catch (e: Exception) {
            Logger.error("NetworkUtils", "Error checking internet connectivity: ${e.message}")
            if (showErrorToast) showToast()
            false
        }
    }
}