package com.example.smartstatus

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btnOverlay = Button(this).apply { text = "Enable Overlay Permission" }
        val btnAccessibility = Button(this).apply { text = "Enable Accessibility" }

        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(btnOverlay)
            addView(btnAccessibility)
        }
        setContentView(layout)

        btnOverlay.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else Toast.makeText(this, "Overlay sudah diizinkan", Toast.LENGTH_SHORT).show()
        }

        btnAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            Toast.makeText(this, "Aktifkan SmartStatusPlayer di Accessibility", Toast.LENGTH_LONG).show()
        }
    }
}
