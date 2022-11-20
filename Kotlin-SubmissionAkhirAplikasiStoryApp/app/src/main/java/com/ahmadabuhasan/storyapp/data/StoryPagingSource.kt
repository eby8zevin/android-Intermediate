package com.ahmadabuhasan.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ahmadabuhasan.storyapp.api.ApiService
import com.ahmadabuhasan.storyapp.model.ListStory
import com.ahmadabuhasan.storyapp.utils.SessionManager

class StoryPagingSource(
    private var sharedPref: SessionManager,
    private val apiService: ApiService
) : PagingSource<Int, ListStory>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStory> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val token = "Bearer ${sharedPref.getToken().token.toString()}"
            println(token)
            Log.d("StoryPagingSource", "Get All Stories")
            val responseData = apiService.allStory(token, page, params.loadSize)
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )

        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}