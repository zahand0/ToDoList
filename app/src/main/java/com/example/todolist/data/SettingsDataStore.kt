package com.example.todolist.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


private const val TODO_LIST_PREFERENCES_NAME = "todo_list_preferences"

// Create a DataStore instance using the preferencesDataStore delegate, with the Context as
// receiver.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = TODO_LIST_PREFERENCES_NAME
)

class SettingsDataStore(context: Context) {
    private val SHOW_DONE_TASKS = booleanPreferencesKey("show_done_tasks")

    val preferenceFlow: Flow<Boolean> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[SHOW_DONE_TASKS] ?: true

        }

    suspend fun saveLayoutToPreferenceStore(isLinearLayoutManager: Boolean, context: Context) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_DONE_TASKS] = isLinearLayoutManager

        }
    }
}