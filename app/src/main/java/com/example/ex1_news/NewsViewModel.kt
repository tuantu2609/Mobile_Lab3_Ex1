package com.example.ex1_news

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.lifecycle.viewModelScope

class NewsViewModel(
    private val service: NewsApiService,
    private val analyzer: SentimentAnalyzer
) : ViewModel() {
    val articles = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { NewsPagingSource(service, "1ecc276ccdc04bfa84410e8813545b42", analyzer) }
    ).flow.cachedIn(viewModelScope)
}
