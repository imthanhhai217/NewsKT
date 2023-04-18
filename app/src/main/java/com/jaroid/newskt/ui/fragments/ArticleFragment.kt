package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.jaroid.newskt.R
import com.jaroid.newskt.databinding.FragmentArticleBinding
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.ui.NewsViewModel

class ArticleFragment : Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: FragmentArticleBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        binding = FragmentArticleBinding.bind(view)

        var article: Article?
        arguments?.let { it ->
            article = it.getSerializable("article") as Article?
            article?.let {
                binding.webView.apply {
                    webViewClient = WebViewClient()
                    loadUrl(it.url)
                }
            }
        }
    }
}