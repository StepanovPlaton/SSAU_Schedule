package com.example.ssau_schedule.data.source

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.authStore by preferencesDataStore(name = "auth")

object AuthStoreKeys {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
}