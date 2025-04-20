package com.example.ex1_news

data class NewsResponse(
    val articles: List<Article>,
    val totalResults: Int
)

data class Article(
    val title: String,
    val content: String?,
    val urlToImage: String?,
    val publishedAt: String
)
