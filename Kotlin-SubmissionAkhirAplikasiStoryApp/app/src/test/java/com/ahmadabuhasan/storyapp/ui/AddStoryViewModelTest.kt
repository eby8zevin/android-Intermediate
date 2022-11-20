package com.ahmadabuhasan.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ResponseAddStory
import com.ahmadabuhasan.storyapp.utils.DataDummy
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.utils.getOrAwaitValue
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class AddStoryViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: StoryViewModel

    private val dummyAddStory = DataDummy.generateDummyAddNewStory()

    private val token = "token"

    @Before
    fun setUp() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun `Add Story is Success`() {
        val description = "description".toRequestBody("text/plain".toMediaType())
        val file = Mockito.mock(File::class.java)
        val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )

        val expectedAddNewStory = MutableLiveData<Result<ResponseAddStory>>()
        expectedAddNewStory.value = Result.Success(dummyAddStory)
        `when`(repository.srAddNewStory(token, imageMultipart, description)).thenReturn(
            expectedAddNewStory
        )

        val actualStory =
            viewModel.vmAddNewStory(token, imageMultipart, description).getOrAwaitValue()

        Mockito.verify(repository).srAddNewStory(token, imageMultipart, description)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
    }
}