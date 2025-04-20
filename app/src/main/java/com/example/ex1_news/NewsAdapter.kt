package com.example.ex1_news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import android.graphics.Color

class NewsAdapter : PagingDataAdapter<ArticleWithSentiment, NewsAdapter.NewsViewHolder>(ARTICLE_COMPARATOR) {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = getItem(position) ?: return

        holder.tvTitle.text = item.article.title
        holder.tvDescription.text = item.article.content
        holder.ivImage.load(item.article.urlToImage)

        val color = if (item.sentiment.positive > item.sentiment.negative) Color.GREEN else Color.RED
        holder.itemView.setBackgroundColor(color)
    }

    companion object {
        private val ARTICLE_COMPARATOR = object : DiffUtil.ItemCallback<ArticleWithSentiment>() {
            override fun areItemsTheSame(oldItem: ArticleWithSentiment, newItem: ArticleWithSentiment) =
                oldItem.article.title == newItem.article.title

            override fun areContentsTheSame(oldItem: ArticleWithSentiment, newItem: ArticleWithSentiment) =
                oldItem == newItem
        }
    }
}
