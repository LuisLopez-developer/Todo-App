package com.example.todoapp.ui.utils

fun extractCleanRoute(fullRoute: String): String {
    // Buscamos el último punto en la ruta
    val lastDotIndex = fullRoute.lastIndexOf('.')
    if (lastDotIndex == -1) {
        // No se encontró un punto, devolvemos la ruta completa
        return fullRoute
    }

    // Extraemos la parte después del último punto
    val afterLastDot = fullRoute.substring(lastDotIndex + 1)

    // Eliminamos cualquier parte que empiece con caracteres especiales (como $, {, /, @, etc.)
    val cleanedRoute = afterLastDot.split(Regex("[\${/\\[\\]()@]")).firstOrNull() ?: afterLastDot

    return cleanedRoute
}