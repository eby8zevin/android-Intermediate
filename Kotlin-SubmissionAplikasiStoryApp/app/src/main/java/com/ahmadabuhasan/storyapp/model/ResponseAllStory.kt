package com.ahmadabuhasan.storyapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseAllStory(

    @SerializedName("error")
    val error: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("listStory")
    val listStory: ArrayList<ListStory>

) {
    @Parcelize
    data class ListStory(

        @SerializedName("id")
        val id: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("description")
        val description: String,

        @SerializedName("photoUrl")
        val photoUrl: String,

        @SerializedName("createdAt")
        val createdAt: String
    ) : Parcelable
}