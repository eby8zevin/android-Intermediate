package com.ahmadabuhasan.storyapp.api

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(): ApiService {

        val client = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            client.addInterceptor(loggingInterceptor)
            client.connectTimeout(150, TimeUnit.SECONDS)
            client.readTimeout(150, TimeUnit.SECONDS)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        return retrofit.create(ApiService::class.java)

    }
}