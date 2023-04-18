package com.jaroid.newskt.api

import com.example.shoppingkt.contants.Constant
import com.jaroid.newskt.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = Constant.API_KEY
    ): Response<NewsResponse>
    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q")  keywords: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = Constant.API_KEY
    ): Response<NewsResponse>
}