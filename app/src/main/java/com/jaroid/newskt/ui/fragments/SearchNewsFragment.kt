package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingkt.contants.Constant
import com.jaroid.newskt.R
import com.jaroid.newskt.adapters.NewsAdapter
import com.jaroid.newskt.databinding.FragmentSearchNewsBinding
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.ui.NewsViewModel
import com.jaroid.newskt.utils.BaseResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var binding: FragmentSearchNewsBinding
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        binding = FragmentSearchNewsBinding.bind(view)

        initView()
        newsViewModel.searchNewsResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseResponse.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        updateData(response.data.articles)
                    }
                }

                is BaseResponse.Error -> {
                    hideProgressBar()
                }

                is BaseResponse.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun initView() {
        setupRecyclerView()
        searchViewTextChangeListener()
    }

    private fun searchViewTextChangeListener() {
        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constant.SEARCH_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        this.newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun updateData(articles: List<Article>) {
        newsAdapter.differ.submitList(articles)
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
    }
}