package com.jaroid.newskt.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.jaroid.newskt.R
import com.jaroid.newskt.adapters.NewsAdapter
import com.jaroid.newskt.databinding.FragmentSavedNewsBinding
import com.jaroid.newskt.models.Article
import com.jaroid.newskt.ui.NewsActivity
import com.jaroid.newskt.ui.NewsViewModel

class SaveNewsFragment : Fragment(R.layout.fragment_saved_news) {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var saveAdapter: NewsAdapter
    lateinit var binding: FragmentSavedNewsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSavedNewsBinding.bind(view)
        newsViewModel = (activity as NewsActivity).newsViewModel
        navHostFragment =
            (activity as NewsActivity).supportFragmentManager.findFragmentById(R.id.newsNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        initView()
        newsViewModel.getSaveNews().observe(viewLifecycleOwner, Observer {
            updateData(it)
        })
    }

    private fun updateData(it: List<Article>?) {
        saveAdapter.differ.submitList(it)
    }

    private fun initView() {
        setupRecyclerView()

    }

    private fun articleOnClickListener() {
        saveAdapter.setOnItemArticleClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_saveNewsFragment_to_articleFragment, bundle
            )
        }
    }

    private fun setupRecyclerView() {
        saveAdapter = NewsAdapter()
        binding.rvSavedNews.apply {
            adapter = saveAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = saveAdapter.differ.currentList[position]
                newsViewModel.deleteArticleSaved(article)
                Snackbar.make(
                    viewHolder.itemView, "Successfully deleted article", Snackbar.LENGTH_SHORT
                ).apply {
                    setAction("Undo") {
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        articleOnClickListener()

    }
}