package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppingkt.contants.Constant
import com.jaroid.newskt.R
import com.jaroid.newskt.adapters.NewsAdapter
import com.jaroid.newskt.databinding.FragmentBreakingNewsBinding
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.`object`.BaseResponse
import com.jaroid.newskt.ui.NewsViewModel

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
                        //toList() bởi vì diffUtil k hoạt động với mutableList
                        updateData(response.data.articles.toList())
                        val totalPage = response.data.totalResults / Constant.QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.breakingNewPage == totalPage
                        if (isLastPage) {
                            binding.rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
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
        isLoading = false
        binding.paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        isLoading = true
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun initView() {
        setupRecyclerView()
    }

    private fun articleOnClickListener() {
        newsAdapter.setOnItemArticleClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment, bundle
            )
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }

        articleOnClickListener()
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
                newsViewModel.getBreakingNews("us")
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