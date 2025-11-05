package com.example.smartstatus

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
import android.util.Log

class WAAccessibilityService : AccessibilityService() {

    private val TAG = "WAAccService"
    private val supportedHosts = listOf("facebook.com","fb.watch","x.com","twitter.com","instagram.com","t.co","youtube.com","youtu.be","tiktok.com")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: return

        // Hanya pantau WhatsApp package event
        if (!(pkg.contains("com.whatsapp"))) return

        // Kita coba dapatkan root window dan cari teks yang mengandung link
        val root = rootInActiveWindow ?: return

        try {
            val foundUrl = findUrlInNode(root)
            if (foundUrl != null) {
                Log.d(TAG, "Found URL: $foundUrl")
                // panggil service overlay untuk fetch & play
                val i = Intent(this, OverlayPlayerService::class.java).apply {
                    putExtra("video_source_url", foundUrl)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startService(i)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scanning nodes", e)
        }
    }

    private fun findUrlInNode(node: AccessibilityNodeInfo): String? {
        // DFS through children to find text with supported hosts
        val text = node.text?.toString()
        if (text != null) {
            for (h in supportedHosts) {
                if (text.contains(h, ignoreCase = true)) {
                    // extract whole URL pattern (simple)
                    val urlRegex = "(https?://[\\w\\-./?=&%#]+)".toRegex()
                    val match = urlRegex.find(text)
                    if (match != null) return match.value
                }
            }
        }
        for (i in 0 until node.childCount) {
            val c = node.getChild(i) ?: continue
            val r = findUrlInNode(c)
            if (r != null) return r
        }
        return null
    }

    override fun onInterrupt() {}
}
