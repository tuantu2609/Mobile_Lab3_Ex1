package com.example.ex1_news

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class SentimentAnalyzer {

    private val client = OkHttpClient()
    private val apiKey = "AIzaSyAhaUaZNoCEEkmeyIHFcejvhulWI0KPlt4"
    private val apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun analyze(text: String): SentimentResult {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    Analyze the following text and classify the sentiment as Positive, Neutral, or Negative.
                    Just respond with "Positive", "Neutral", or "Negative" only.
                    Text: "$text"
                """.trimIndent()

                val partsArray = JSONArray().put(JSONObject().put("text", prompt))
                val contentsArray = JSONArray().put(JSONObject().put("parts", partsArray))
                val requestBodyJson = JSONObject().put("contents", contentsArray)

                val requestBody = requestBodyJson.toString()
                    .toRequestBody("application/json".toMediaTypeOrNull())

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception("Sentiment API call failed: ${response.code}")
                }

                val body = response.body?.string() ?: throw Exception("Empty response")
                val json = JSONObject(body)

                val candidates = json.getJSONArray("candidates")
                val content = candidates.getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val sentimentResult = when {
                    content.contains("Positive", ignoreCase = true) -> SentimentResult(positive = 1f, negative = 0f)
                    content.contains("Negative", ignoreCase = true) -> SentimentResult(positive = 0f, negative = 1f)
                    content.contains("Neutral", ignoreCase = true) -> SentimentResult(positive = 0.5f, negative = 0.5f)
                    else -> SentimentResult(positive = 0.5f, negative = 0.5f)
                }

                sentimentResult
            } catch (e: Exception) {
                e.printStackTrace()
                SentimentResult(positive = 0.5f, negative = 0.5f)
            }
        }
    }
}
