package com.ahmadabuhasan.storyapp.api

import com.ahmadabuhasan.storyapp.model.ResponseAddStory
import com.ahmadabuhasan.storyapp.model.ResponseAllStory
import com.ahmadabuhasan.storyapp.model.ResponseLogin
import com.ahmadabuhasan.storyapp.model.ResponseRegister
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponseRegister

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): ResponseLogin

    @Multipart
    @POST("stories")
    suspend fun addNewStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): ResponseAddStory

    @GET("stories")
    suspend fun allStory(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ResponseAllStory

    @GET("stories")
    suspend fun allStoryLocation(
        @Header("Authorization") token: String,
        @Query("location") location: Int,
    ): ResponseAllStory
}