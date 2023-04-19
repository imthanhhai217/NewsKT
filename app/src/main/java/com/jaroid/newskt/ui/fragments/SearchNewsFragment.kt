package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel = (activity as NewsActivity).newsViewModel
        binding = FragmentSearchNewsBinding.bind(view)
        navHostFragment =
            (activity as NewsActivity).supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        initView()
        newsViewModel.searchNewsResult.observe(viewLifecycleOwner) { response ->
            when (response) {
                is BaseResponse.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        updateData(response.data.articles.toList())
                        val totalPage = response.data.totalResults / Constant.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.searchNewsPage == totalPage
                        if (isLastPage) {
                            binding.rvSearchNews.setPadding(0, 0, 0, 0)
                        }
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
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
        articleOnClickListener()
    }

    private fun articleOnClickListener() {
        newsAdapter.setOnItemArticleClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            navController.navigate(
                R.id.action_searchNewsFragment_to_articleFragment, bundle
            )
        }
    }

    private fun showProgressBar() {
        isLoading = true
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun updateData(articles: List<Article>) {
        newsAdapter.differ.submitList(articles)
    }

    private fun hideProgressBar() {
        isLoading = false
        binding.paginationProgressBar.visibility = View.GONE
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
            val childCount = layoutManager.childCount
            val itemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItem + childCount >= itemCount
            val isNotAtBeginning = firstVisibleItem >= 0
            val isTotalMoreThanVisible = itemCount >= Constant.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                newsViewModel.searchNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}