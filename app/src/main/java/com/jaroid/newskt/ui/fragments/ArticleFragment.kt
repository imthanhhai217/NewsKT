package com.jaroid.newskt.ui.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
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
                val client = object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        binding.llLoading.visibility = View.VISIBLE
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        binding.llLoading.visibility = View.GONE
                    }
                }
                binding.webView.apply {
                    webViewClient = client
                    loadUrl(it.url)
                }
            }
        }
    }
}