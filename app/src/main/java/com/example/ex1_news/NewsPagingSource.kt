package com.example.ex1_news

import androidx.paging.PagingSource
import androidx.paging.PagingState

class NewsPagingSource(
    private val service: NewsApiService,
    private val apiKey: String,
    private val analyzer: SentimentAnalyzer
) : PagingSource<Int, ArticleWithSentiment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleWithSentiment> {
        return try {
            val page = params.key ?: 1
            val response = service.getArticles(
                query = "android",
                pageSize = params.loadSize,
                page = page,
                apiKey = apiKey
            )

            val articlesWithSentiment = response.articles.map { article ->
                val sentiment = analyzer.analyze(article.content ?: article.title)
                ArticleWithSentiment(article, sentiment)
            }

            LoadResult.Page(
                data = articlesWithSentiment,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articlesWithSentiment.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleWithSentiment>): Int? = null
}