package com.ahmadabuhasan.storyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ListStory
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryViewModel(private val repository: StoryRepository) : ViewModel() {

    fun vmRegister(name: String, email: String, password: String) =
        repository.srRegister(name, email, password)

    fun vmLogin(email: String, password: String) =
        repository.srLogin(email, password)

    fun vmAddNewStory(token: String, file: MultipartBody.Part, desc: RequestBody) =
        repository.srAddNewStory(token, file, desc)

    fun vmListStory(): LiveData<PagingData<ListStory>> =
        repository.srGetStory().cachedIn(viewModelScope)

    fun vmStoryLocation(token: String) = repository.srStoryLocation(token)
}