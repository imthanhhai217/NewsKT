package com.jaroid.newskt.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jaroid.newskt.models.NewsResponse
import com.jaroid.newskt.repositories.NewsRepository
import com.jaroid.newskt.utils.BaseResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(val newsRepository: NewsRepository) : ViewModel() {

    val breakingNewsResult: MutableLiveData<BaseResponse<NewsResponse>> = MutableLiveData()
    var breakingNewPage = 1

    val searchNewsResult: MutableLiveData<BaseResponse<NewsResponse>> = MutableLiveData()
    val searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    private fun getBreakingNews(countryCode: String) {
        viewModelScope.launch {
            breakingNewsResult.postValue(BaseResponse.Loading())
            val response = newsRepository.getBreakingNews(countryCode, breakingNewPage)
            breakingNewsResult.postValue(handleBreakingNewsResponse(response))
        }
    }

    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): BaseResponse<NewsResponse> {
        if (response.isSuccessful && response.code() == 200) {
            response.body()?.let {
                return BaseResponse.Success(it)
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
            response.body()?.let {
                return BaseResponse.Success(it)
            }
        }
        return BaseResponse.Error(response.message())
    }
}