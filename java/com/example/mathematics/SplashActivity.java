package com.example.mathematics;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private VideoView videoView;
    private FrameLayout splashContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.videoView);
        splashContainer = findViewById(R.id.splashContainer);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.splash;
        videoView.setVideoURI(Uri.parse(videoPath));
        
        // Set video scaling
        videoView.setOnPreparedListener(mp -> {
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        });
        
        videoView.start();

        // Set click listener for the splash container
        splashContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainMenu();
            }
        });

        // Set completion listener for the video
        videoView.setOnCompletionListener(mp -> navigateToMainMenu());
    }

    private void navigateToMainMenu() {
        Intent intent = new Intent(SplashActivity.this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }
} 