package com.example.project_f1;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_ID = "video_id";

    private YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full-screen immersive
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().setBackgroundDrawableResource(android.R.color.black);

        setContentView(R.layout.activity_video_player);

        String videoId = getIntent().getStringExtra(EXTRA_VIDEO_ID);
        ProgressBar spinner = findViewById(R.id.videoSpinner);
        ImageButton btnClose = findViewById(R.id.btnCloseVideo);
        youTubePlayerView = findViewById(R.id.youtubeFullPlayer);

        getLifecycle().addObserver(youTubePlayerView);

        btnClose.setOnClickListener(v -> finish());

        if (videoId != null) {
            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(YouTubePlayer youTubePlayer) {
                    spinner.setVisibility(View.GONE);
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        youTubePlayerView.release();
    }
}
