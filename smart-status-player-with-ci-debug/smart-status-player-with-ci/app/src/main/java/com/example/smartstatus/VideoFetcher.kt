package com.example.smartstatus

import android.content.Context
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import kotlinx.coroutines.withContext

object VideoFetcher {
    private const val BACKEND_BASE = "http://YOUR_SERVER_IP:8080" // change to your server or domain

    suspend fun getDirectVideoUrl(context: Context, sourceUrl: String): String? {
        return withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val encoded = java.net.URLEncoder.encode(sourceUrl, "UTF-8")
                val api = "$BACKEND_BASE/get_video?url=$encoded"
                val conn = URL(api).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 15000
                conn.readTimeout = 15000
                val code = conn.responseCode
                if (code == 200) {
                    val text = conn.inputStream.bufferedReader().use { it.readText() }
                    val obj = JSONObject(text)
                    return@withContext obj.optString("video_url", null)
                } else {
                    Log.e("VideoFetcher", "Backend response $code")
                }
            } catch (e: Exception) {
                Log.e("VideoFetcher", "Error", e)
            }
            return@withContext null
        }
    }
}
