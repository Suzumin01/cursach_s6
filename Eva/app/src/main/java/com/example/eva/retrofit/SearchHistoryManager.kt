package com.example.eva.retrofit

import android.content.Context
import androidx.core.content.edit

class SearchHistoryManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        "search_history",
        Context.MODE_PRIVATE
    )
    private val maxHistorySize = 10

    fun saveQuery(query: String) {
        val history = getHistory().toMutableList()

        history.remove(query)
        history.add(0, query)
        if (history.size > maxHistorySize) {
            history.removeAt(history.size - 1)
        }

        sharedPreferences.edit {
            putStringSet("history", history.toSet())
        }
    }

    fun getHistory(): List<String> {
        return sharedPreferences.getStringSet("history", emptySet())?.toList() ?: emptyList()
    }

    fun clearHistory() {
        sharedPreferences.edit { clear() }
    }
}