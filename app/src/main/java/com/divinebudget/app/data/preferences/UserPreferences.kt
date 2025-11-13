package com.divinebudget.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    
    companion object {
        private val CURRENCY_KEY = stringPreferencesKey("currency")
        private val REMINDERS_ENABLED_KEY = booleanPreferencesKey("reminders_enabled")
        private val THEME_KEY = stringPreferencesKey("theme")
        private val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }
    
    val currency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY_KEY] ?: "$"
    }
    
    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMINDERS_ENABLED_KEY] ?: true
    }
    
    val theme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: "light"
    }
    
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }
    
    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }
    
    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDERS_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = false
        }
    }
}

