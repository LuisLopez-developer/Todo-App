package com.example.todoapp.holidays.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

object NetworkUtils {
    fun isInternetAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            if (network == null) {
                Log.e("NetworkUtils", "No active network")
                return false
            }

            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities == null) {
                Log.e("NetworkUtils", "No network capabilities available")
                return false
            }

            // Check if the network has internet connectivity
            val hasInternet = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

            if (!hasInternet) {
                Log.e("NetworkUtils", "No internet transport available")
            }

            hasInternet
        } catch (e: Exception) {
            Log.e("NetworkUtils", "Error checking internet connectivity: ${e.message}")
            false
        }
    }
}