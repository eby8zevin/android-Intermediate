package com.ahmadabuhasan.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.utils.DataDummy
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.utils.getOrAwaitValue
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: StoryViewModel

    private val dummyStoryLocation = DataDummy.generateDummyLocation()

    private val token = "token"

    @Before
    fun setUp() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun `All Story Location is Success`() {
        val expectedStoryLocation = MutableLiveData<Result<ResponseAllStory>>()
        expectedStoryLocation.value = Result.Success(dummyStoryLocation)

        `when`(repository.srStoryLocation(token)).thenReturn(expectedStoryLocation)

        val actualStory = viewModel.vmStoryLocation(token).getOrAwaitValue()
        Mockito.verify(repository).srStoryLocation(token)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }
}