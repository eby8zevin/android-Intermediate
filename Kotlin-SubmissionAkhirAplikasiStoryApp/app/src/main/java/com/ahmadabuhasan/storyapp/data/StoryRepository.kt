package com.ahmadabuhasan.storyapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ahmadabuhasan.storyapp.api.ApiService
import com.ahmadabuhasan.storyapp.model.*
import com.ahmadabuhasan.storyapp.utils.SessionManager
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val sharedPref: SessionManager, private val apiService: ApiService) {

    fun srGetStory(): LiveData<PagingData<ListStory>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = {
                StoryPagingSource(sharedPref, apiService)
            }
        ).liveData
    }

    fun srRegister(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<ResponseRegister>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        Log.d("StoryRepository", "Register")
    }

    fun srLogin(email: String, password: String): LiveData<Result<ResponseLogin>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        Log.d("StoryRepository", "Login")
    }

    fun srAddNewStory(
        token: String,
        file: MultipartBody.Part,
        desc: RequestBody
    ): LiveData<Result<ResponseAddStory>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addNewStory(token, file, desc)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        Log.d("StoryRepository", "AddNewStory")
    }

    fun srStoryLocation(token: String): LiveData<Result<ResponseAllStory>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.allStoryLocation(token, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
        Log.d("StoryRepository", "StoryLocation")
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            sharedPref: SessionManager,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(sharedPref, apiService)
            }.also { instance = it }
    }
}