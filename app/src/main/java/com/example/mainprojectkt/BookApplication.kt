package com.example.mainprojectkt

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

class BookApplication: Application() {
    lateinit var dataStore: DataStore<Preferences>
        private set

    override fun onCreate() {
        super.onCreate()
        dataStore = PreferenceDataStoreFactory.create {
            preferencesDataStoreFile("account")
        }
    }
}