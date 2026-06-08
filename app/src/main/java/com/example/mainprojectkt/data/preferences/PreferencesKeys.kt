package com.example.mainprojectkt.data.preferences

import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val USER_ID = longPreferencesKey("userId")
    val THEME_MODE = stringPreferencesKey("theme_mode")
}