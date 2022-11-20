package com.ahmadabuhasan.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ResponseRegister
import com.ahmadabuhasan.storyapp.utils.DataDummy
import com.ahmadabuhasan.storyapp.utils.getOrAwaitValue
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
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

@RunWith(MockitoJUnitRunner::class)
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: StoryViewModel

    private var dummyRegister = DataDummy.generateDummyRegister()

    private val name = "name"
    private val email = "email@domain.com"
    private val password = "password"

    @Before
    fun setUp() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun `Register is Success`() {
        val expectedRegister = MutableLiveData<Result<ResponseRegister>>()
        expectedRegister.value = Result.Success(dummyRegister)
        `when`(repository.srRegister(name, email, password)).thenReturn(expectedRegister)

        val actualUser = viewModel.vmRegister(name, email, password).getOrAwaitValue()

        Mockito.verify(repository).srRegister(name, email, password)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
    }
}