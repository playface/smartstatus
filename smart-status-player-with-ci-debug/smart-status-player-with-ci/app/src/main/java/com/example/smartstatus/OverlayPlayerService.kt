package com.example.smartstatus

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.MediaItem
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.*
import android.widget.ImageButton

class OverlayPlayerService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private var player: SimpleExoPlayer? = null
    private val TAG = "OverlayPlayerService"
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var isPlaying = true

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sourceUrl = intent?.getStringExtra("video_source_url") ?: return START_NOT_STICKY
        // Fetch direct video URL via backend
        scope.launch {
            try {
                val directUrl = VideoFetcher.getDirectVideoUrl(applicationContext, sourceUrl)
                if (directUrl != null) {
                    showOverlayAndPlay(directUrl)
                } else stopSelf()
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching video", e)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun showOverlayAndPlay(videoUrl: String) {
        if (overlayView != null) return
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.overlay_player, null)

        val playerView = view.findViewById<PlayerView>(R.id.player_view)
        val btnPlay = view.findViewById<ImageButton>(R.id.btn_play_pause)
        val btnClose = view.findViewById<ImageButton>(R.id.btn_close)

        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        isPlaying = true
        btnPlay.setImageResource(android.R.drawable.ic_media_pause)

        btnPlay.setOnClickListener {
            if (isPlaying) {
                player?.playWhenReady = false
                isPlaying = false
                btnPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                player?.playWhenReady = true
                isPlaying = true
                btnPlay.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        btnClose.setOnClickListener {
            removeOverlay()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER
        overlayView = view
        windowManager.addView(overlayView, params)

        // auto remove after 120s safety, or you can detect WA window change to remove
        Handler(mainLooper).postDelayed({ removeOverlay() }, 120_000)
    }

    private fun removeOverlay() {
        try {
            overlayView?.let { windowManager.removeView(it) }
        } catch (e: Exception) { }
        overlayView = null
        player?.release()
        player = null
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
