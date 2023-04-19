package com.jaroid.newskt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jaroid.newskt.databinding.ItemArticlePreviewBinding
import com.jaroid.newskt.models.Article

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.jaroid.newskt.R.layout.item_article_preview, parent, false)
        val binding = ItemArticlePreviewBinding.bind(view)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            article?.let {
                binding.article = it
                binding.root.setOnClickListener {
                    onItemArticleClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }

    private var onItemArticleClickListener: ((Article) -> Unit)? = null

    fun setOnItemArticleClickListener(listener: (Article) -> Unit) {
        onItemArticleClickListener = listener
    }
}