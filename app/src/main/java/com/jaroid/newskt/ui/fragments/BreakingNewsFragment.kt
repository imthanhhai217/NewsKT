package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaroid.newskt.R
import com.jaroid.newskt.adapters.NewsAdapter
import com.jaroid.newskt.databinding.FragmentBreakingNewsBinding
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.ui.NewsViewModel
import com.jaroid.newskt.utils.BaseResponse

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    lateinit var binding: FragmentBreakingNewsBinding
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBreakingNewsBinding.bind(view)
        newsViewModel = (activity as NewsActivity).newsViewModel

        initView()
        newsViewModel.breakingNewsResult.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is BaseResponse.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        updateData(response.data.articles)
                    }
                }

                is BaseResponse.Error -> {

                }

                is BaseResponse.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun updateData(data: List<Article>) {
        newsAdapter.differ.submitList(data)
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun initView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }
}