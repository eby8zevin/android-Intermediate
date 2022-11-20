package com.ahmadabuhasan.storyapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ahmadabuhasan.storyapp.adapter.StoryAdapter
import com.ahmadabuhasan.storyapp.data.StoryRepository
import com.ahmadabuhasan.storyapp.model.ListStory
import com.ahmadabuhasan.storyapp.utils.DataDummy
import com.ahmadabuhasan.storyapp.utils.MainDispatcherRule
import com.ahmadabuhasan.storyapp.utils.getOrAwaitValue
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var repository: StoryRepository

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `All Story is Success`() = runTest {
        val dummyAllStory = DataDummy.generateDummy()
        val data: PagingData<ListStory> = StoryPagingSource.snapshot(dummyAllStory)

        val expectedAllStory = MutableLiveData<PagingData<ListStory>>()
        expectedAllStory.value = data

        `when`(repository.srGetStory()).thenReturn(expectedAllStory)
        val viewModel = StoryViewModel(repository)

        val actualStory: PagingData<ListStory> = viewModel.vmListStory().getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        assertNotNull(differ.snapshot())
        assertEquals(dummyAllStory, differ.snapshot())
        assertEquals(dummyAllStory.size, differ.snapshot().size)
        assertEquals(dummyAllStory[0].name, differ.snapshot()[0]?.name)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ListStory>>>() {
    companion object {
        fun snapshot(items: List<ListStory>): PagingData<ListStory> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStory>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStory>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}