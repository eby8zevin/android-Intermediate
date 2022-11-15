package com.ahmadabuhasan.storyapp.model

import com.google.gson.annotations.SerializedName

data class ResponseAddStory(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,
)