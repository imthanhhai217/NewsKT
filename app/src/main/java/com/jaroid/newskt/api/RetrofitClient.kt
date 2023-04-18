package com.jaroid.newskt.api

import com.example.shoppingkt.contants.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        private val instances by lazy {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
            Retrofit.Builder().baseUrl(Constant.BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()).build()

        }

        val getNewsAPI: NewsAPI by lazy {
            instances.create(NewsAPI::class.java)
        }
    }
}