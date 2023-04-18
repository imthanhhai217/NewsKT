package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jaroid.newskt.R
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.ui.NewsViewModel

class SaveNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        navHostFragment =
            (activity as NewsActivity).supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}