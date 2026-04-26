package com.diary.moonpage.core.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.activityDataStore: DataStore<Preferences>
    by preferencesDataStore(name = "activity_prefs")

@Singleton
class ActivityPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ENABLED_CATEGORIES_KEY = stringPreferencesKey("enabled_categories")

        val DEFAULT_ENABLED = setOf("Hobbies", "Emotions", "Meals", "SelfCare")

        val ALL_CATEGORY_KEYS = listOf(
            "Hobbies", "Emotions", "Meals", "SelfCare", "Chores",
            "Events", "People", "Beauty", "Weather", "Health",
            "Work", "Other", "School", "Relationship"
        )
    }

    val enabledCategories: Flow<Set<String>> = context.activityDataStore.data.map { prefs ->
        val saved = prefs[ENABLED_CATEGORIES_KEY]
        if (saved.isNullOrEmpty()) DEFAULT_ENABLED
        else saved.split(",").filter { it.isNotEmpty() }.toSet()
    }

    suspend fun saveEnabledCategories(categories: Set<String>) {
        context.activityDataStore.edit { prefs ->
            prefs[ENABLED_CATEGORIES_KEY] = categories.joinToString(",")
        }
    }
}
