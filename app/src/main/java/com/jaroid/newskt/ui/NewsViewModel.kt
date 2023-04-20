package com.jaroid.newskt.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jaroid.newskt.MyApplication
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.models.NewsResponse
import com.jaroid.newskt.repositories.NewsRepository
import com.jaroid.newskt.utils.BaseResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository, application: Application) :
    AndroidViewModel(
        application
    ) {

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
        if (hasInternetConnection()) {
            viewModelScope.launch {
                breakingNewsResult.postValue(BaseResponse.Loading())
                val response = newsRepository.getBreakingNews(countryCode, breakingNewPage)
                breakingNewsResult.postValue(handleBreakingNewsResponse(response))
            }
        } else {
            breakingNewsResult.postValue(BaseResponse.Error("No internet!"))
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
                return BaseResponse.Success(breakingNewsResponse ?: newsResponse)
            }
        }
        return BaseResponse.Error(response.message())
    }


    fun searchNews(keywords: String) {
        if (hasInternetConnection()) {
            viewModelScope.launch {
                searchNewsResult.postValue(BaseResponse.Loading())
                var response = newsRepository.searchNews(keywords, searchNewsPage)
                searchNewsResult.postValue(handleSearchNewsResponse(response))
            }
        } else {
            searchNewsResult.postValue(BaseResponse.Error("No Internet!"))
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
                return BaseResponse.Success(searchNewsResponse ?: newsResponse)
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

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<MyApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //Api 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val accessNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(accessNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}