package com.playface.v2;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private EditText edtUrl;
    private Button btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);
        edtUrl = findViewById(R.id.edt_url);
        btnPlay = findViewById(R.id.btn_play);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);
        videoView.setMediaController(mc);

        btnPlay.setOnClickListener(v -> {
            String url = edtUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Masukkan URL video", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                videoView.setVideoURI(Uri.parse(url));
                videoView.requestFocus();
                videoView.start();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal play: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
