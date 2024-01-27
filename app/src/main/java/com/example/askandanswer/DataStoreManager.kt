package com.example.askandanswer

import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val dataStore = MyApplication.getInstance().dataStore

    private object PreferencesKey {
        val ID = intPreferencesKey("id")
        val NAME = stringPreferencesKey("name")
        val EMAIL = stringPreferencesKey("email")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val id: Flow<Int?> = dataStore.data.catch { e ->
        Log.e("DataStoreManager", "Error from id property.", e)
        emit(emptyPreferences())
    }.map { preferences -> preferences[PreferencesKey.ID] }

    val name: Flow<String?> = dataStore.data.catch { e ->
        Log.e("DataStoreManager", "Error from name property.", e)
        emit(emptyPreferences())
    }.map { preferences -> preferences[PreferencesKey.NAME] }

    val email: Flow<String?> = dataStore.data.catch { e ->
        Log.e("DataStoreManager", "Error from email property.", e)
        emit(emptyPreferences())
    }.map { preferences -> preferences[PreferencesKey.EMAIL] }

    val accessToken: Flow<String?> = dataStore.data.catch { e ->
        Log.e("DataStoreManager", "Error from accessToken property.", e)
        emit(emptyPreferences())
    }.map { preferences -> preferences[PreferencesKey.ACCESS_TOKEN] }

    val refreshToken: Flow<String?> = dataStore.data.catch { e ->
        Log.e("DataStoreManager", "Error from refreshToken property.", e)
        emit(emptyPreferences())
    }.map { preferences -> preferences[PreferencesKey.REFRESH_TOKEN] }

    suspend fun saveUserData(id: Int, name: String, email: String, accessToken: String, refreshToken: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKey.ID] = id
                preferences[PreferencesKey.NAME] = name
                preferences[PreferencesKey.EMAIL] = email
                preferences[PreferencesKey.ACCESS_TOKEN] = accessToken
                preferences[PreferencesKey.REFRESH_TOKEN] = refreshToken
            }
        }
        catch (e: Exception) {
            Log.e("DataStoreManager", "Error from saveUserData method.", e)
            throw e
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        try {
            dataStore.edit { preferences ->
                preferences[PreferencesKey.ACCESS_TOKEN] = accessToken
                preferences[PreferencesKey.REFRESH_TOKEN] = refreshToken
            }
        }
        catch (e: Exception) {
            Log.e("DataStoreManager", "Error from saveTokens method.", e)
            throw e
        }
    }

    suspend fun clear() {
        try {
            dataStore.edit { preferences -> preferences.clear() }
        }
        catch (e: Exception) {
            Log.e("DataStoreManager", "Error from clear method.", e)
            throw e
        }
    }
}