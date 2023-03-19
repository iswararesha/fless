package com.resha.fless.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map

class AppPreference private constructor(private val dataStore: DataStore<Preferences>) {
    fun getStatus() = dataStore.data.map{ preferences ->
        preferences[FIRST_OPEN_STATUS] ?: true
    }

    suspend fun editStatus() {
        dataStore.edit { preferences ->
            preferences[FIRST_OPEN_STATUS] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppPreference? = null

        private val FIRST_OPEN_STATUS = booleanPreferencesKey("firstOpenStatus")

        fun getInstance(dataStore: DataStore<Preferences>): AppPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}