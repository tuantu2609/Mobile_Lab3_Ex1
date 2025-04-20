package com.example.ex1_news

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var viewModel: NewsViewModel
    private lateinit var sentimentAnalyzer: SentimentAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sentimentAnalyzer = SentimentAnalyzer()

        val service = RetrofitInstance.apiService
        viewModel = NewsViewModel(service, sentimentAnalyzer)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        newsAdapter = NewsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = newsAdapter

        lifecycleScope.launch {
            viewModel.articles.collect { pagingData ->
                newsAdapter.submitData(pagingData)
            }
        }
    }
}
