package com.example.todoapp.settings.drive.data

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials

fun getDriveService(accessToken: String): Drive {
    val credentials = GoogleCredentials.create(AccessToken(accessToken, null))
    return Drive.Builder(
        NetHttpTransport(),
        GsonFactory(),
        HttpCredentialsAdapter(credentials)
    )
        .setApplicationName("TodoApp")
        .build()
}