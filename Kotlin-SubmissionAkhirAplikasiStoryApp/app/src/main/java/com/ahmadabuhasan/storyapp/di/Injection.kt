package com.ahmadabuhasan.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ahmadabuhasan.storyapp.api.ApiConfig
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.utils.Constant
import com.ahmadabuhasan.storyapp.utils.SessionManager

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constant.PREFS_NAME)

object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val sharedPref = SessionManager(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(sharedPref, apiService)
    }
}