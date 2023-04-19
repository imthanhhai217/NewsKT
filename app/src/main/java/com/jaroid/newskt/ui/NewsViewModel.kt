package com.jaroid.newskt.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.models.NewsResponse
import com.jaroid.newskt.repositories.NewsRepository
import com.jaroid.newskt.utils.BaseResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    val breakingNewsResult: MutableLiveData<BaseResponse<NewsResponse>> = MutableLiveData()
    var breakingNewPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNewsResult: MutableLiveData<BaseResponse<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            breakingNewsResult.postValue(BaseResponse.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewPage)
            breakingNewsResult.postValue(handleBreakingNewsResponse(response))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): BaseResponse<NewsResponse> {
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let { newsResponse ->
                breakingNewPage++
                //Loading first time
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = newsResponse
                } else {
                    val oldData = breakingNewsResponse!!.articles
                    val newData = newsResponse.articles
                    oldData.addAll(newData)
                }
                return BaseResponse.Success(breakingNewsResponse?:newsResponse)
            }
        }
        return BaseResponse.Error(response.message())
    }


    fun searchNews(keywords: String) {
        viewModelScope.launch {
            searchNewsResult.postValue(BaseResponse.Loading())
            var response = newsRepository.searchNews(keywords, searchNewsPage)
            searchNewsResult.postValue(handleSearchNewsResponse(response))
        }

    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): BaseResponse<NewsResponse> {
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let { newsResponse ->
                searchNewsPage++
                //Loading first time
                if (searchNewsResponse == null) {
                    searchNewsResponse = newsResponse
                } else {
                    val oldData = searchNewsResponse!!.articles
                    val newData = newsResponse.articles
                    oldData.addAll(newData)
                }
                return BaseResponse.Success(searchNewsResponse?:newsResponse)
            }
        }
        return BaseResponse.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSaveNews() = newsRepository.getSaveNews()

    fun deleteArticleSaved(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticleSaved(article)
    }
}