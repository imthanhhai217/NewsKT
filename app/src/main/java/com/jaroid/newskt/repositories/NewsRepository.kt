package com.jaroid.newskt.repositories

import com.jaroid.newskt.api.RetrofitClient
import com.jaroid.newskt.db.ArticleDatabase
import com.jaroid.newskt.models.Article

class NewsRepository(val articleDatabase: ArticleDatabase) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitClient.getNewsAPI.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(keywords: String, pageNumber: Int) =
        RetrofitClient.getNewsAPI.searchNews(keywords, pageNumber)

    suspend fun upsert(article: Article) = articleDatabase.getArticleDAO().upsert(article)

    fun getSaveNews() = articleDatabase.getArticleDAO().getAllArticles()

    suspend fun deleteArticleSaved(article: Article) =
        articleDatabase.getArticleDAO().deleteArticle(article)
}