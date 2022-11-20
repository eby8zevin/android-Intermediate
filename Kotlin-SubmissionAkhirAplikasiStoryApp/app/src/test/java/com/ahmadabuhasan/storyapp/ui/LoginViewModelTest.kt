package com.ahmadabuhasan.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ResponseLogin
import com.ahmadabuhasan.storyapp.utils.DataDummy
import com.ahmadabuhasan.storyapp.data.Result
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
class LoginViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: StoryRepository
    private lateinit var viewModel: StoryViewModel

    private val dummyLogin = DataDummy.generateDummyLogin()

    private val email = "email@domain.com"
    private val password = "password"

    @Before
    fun setUp() {
        viewModel = StoryViewModel(repository)
    }

    @Test
    fun `Login is Success`() {
        val expectedLogin = MutableLiveData<Result<ResponseLogin>>()
        expectedLogin.value = Result.Success(dummyLogin)
        `when`(repository.srLogin(email, password)).thenReturn(expectedLogin)

        val actualUser = viewModel.vmLogin(email, password).getOrAwaitValue()

        Mockito.verify(repository).srLogin(email, password)
        assertNotNull(actualUser)
        assertTrue(actualUser is Result.Success)
    }
}