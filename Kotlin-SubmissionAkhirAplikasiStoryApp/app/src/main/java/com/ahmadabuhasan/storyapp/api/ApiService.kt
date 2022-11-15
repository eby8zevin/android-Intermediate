package com.ahmadabuhasan.storyapp.api

import com.ahmadabuhasan.storyapp.model.ResponseAddStory
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.model.ResponseLogin
import com.ahmadabuhasan.storyapp.model.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseRegister>

    @FormUrlEncoded
    @POST("v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<ResponseLogin>

    @Multipart
    @POST("v1/stories")
    fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<ResponseAddStory>

    @GET("v1/stories")
    fun allStory(
        @Header("Authorization") token: String
    ): Call<ResponseAllStory>
}