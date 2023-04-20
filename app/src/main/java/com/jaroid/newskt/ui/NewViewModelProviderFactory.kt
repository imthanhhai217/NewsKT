package com.jaroid.newskt.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jaroid.newskt.repositories.NewsRepository

class NewViewModelProviderFactory(
    private val newsRepository: NewsRepository,
    private val application: Application
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(newsRepository = newsRepository, application) as T
    }
}