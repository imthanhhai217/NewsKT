package com.jaroid.newskt.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.jaroid.newskt.R
import com.jaroid.newskt.databinding.ActivityNewsBinding
import com.jaroid.newskt.db.ArticleDatabase
import com.jaroid.newskt.repositories.NewsRepository

class NewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewsBinding
    lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = layoutInflater.inflate(R.layout.activity_news, null)
        binding = ActivityNewsBinding.bind(view)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val newViewModelProviderFactory = NewViewModelProviderFactory(newsRepository,application)
        newsViewModel = ViewModelProvider(
            this, factory = newViewModelProviderFactory
        )[NewsViewModel::class.java]

    }
}