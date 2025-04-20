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
                    Analyze the following text and classify the sentiment as Positive or Negative.
                    Just respond with "Positive" or "Negative" only.
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

                val positive = if (content.contains("Positive", ignoreCase = true)) 1f else 0f
                val negative = if (content.contains("Negative", ignoreCase = true)) 1f else 0f

                SentimentResult(positive = positive, negative = negative)
            } catch (e: Exception) {
                e.printStackTrace()
                SentimentResult(positive = 0.5f, negative = 0.5f)
            }
        }
    }
}
