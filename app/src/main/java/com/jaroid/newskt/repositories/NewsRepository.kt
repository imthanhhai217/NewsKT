package com.jaroid.newskt.repositories

import com.jaroid.newskt.api.RetrofitClient
import com.jaroid.newskt.db.ArticleDatabase

class NewsRepository(val articleDatabase: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitClient.getNewsAPI.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(keywords: String, pageNumber: Int) =
        RetrofitClient.getNewsAPI.searchNews(keywords, pageNumber)
}