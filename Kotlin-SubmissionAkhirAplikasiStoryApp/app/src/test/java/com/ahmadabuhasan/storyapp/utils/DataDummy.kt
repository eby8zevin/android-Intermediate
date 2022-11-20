package com.ahmadabuhasan.storyapp.utils

import com.ahmadabuhasan.storyapp.model.*

object DataDummy {

    fun generateDummyRegister(): ResponseRegister {
        return ResponseRegister(
            false,
            "success"
        )
    }

    fun generateDummyLogin(): ResponseLogin {
        return ResponseLogin(
            false,
            "success",
            User(
                "userId",
                "name",
                "token"
            )
        )
    }

    fun generateDummyAddNewStory(): ResponseAddStory {
        return ResponseAddStory(
            false,
            "success"
        )
    }

    fun generateDummy(): List<ListStory> {
        val list = arrayListOf<ListStory>()

        for (i in 0 until 10) {
            val listItem = ListStory(
                "story-FvU4u0Vp2S3PMsFg",
                "Dimas",
                "Lorem Ipsum",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                -10.212,
                -16.002
            )
            list.add(listItem)
        }
        return list
    }

    fun generateDummyLocation(): ResponseAllStory {
        val list: MutableList<ListStory> = arrayListOf()

        for (i in 0..100) {
            val listItem = ListStory(
                "story-FvU4u0Vp2S3PMsFg",
                "Dimas",
                "Lorem Ipsum",
                "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                "2022-01-08T06:34:18.598Z",
                -10.212,
                -16.002
            )
            list.add(listItem)
        }
        return ResponseAllStory(
            false,
            "success",
            list
        )
    }
}