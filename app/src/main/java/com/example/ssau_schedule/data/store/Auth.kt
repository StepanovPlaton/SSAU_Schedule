package com.example.ssau_schedule.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.authStore by preferencesDataStore(name = "auth")

class AuthStore {
    class Keys {
        companion object {
            val AUTH_TOKEN = stringPreferencesKey("auth_token")
        }
    }

    companion object {
        suspend fun setAuthToken(
            token: String,
            context: Context,
        ) { context.authStore.edit { authStore -> authStore[Keys.AUTH_TOKEN] = token } }

        fun setAuthToken(
            token: String,
            context: Context,
            scope: CoroutineScope,
            callback: (() -> Unit)? = null
        ) = scope.launch { setAuthToken(token, context) }.run { callback?.invoke() }

        suspend fun getAuthToken(context: Context) =
            context.authStore.data.map { authStore -> authStore[Keys.AUTH_TOKEN] }.first()

        fun getAuthToken(
            context: Context,
            scope: CoroutineScope,
            callback: (token: String?) -> Unit
        ) = scope.launch { callback(getAuthToken(context)) }
    }
}