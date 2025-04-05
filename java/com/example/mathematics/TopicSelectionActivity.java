package com.example.mathematics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;
import com.google.android.material.card.MaterialCardView;

public class TopicSelectionActivity extends BaseActivity {
    private MaterialCardView cardCompareNumbers;
    private MaterialCardView cardOrderNumbers;
    private MaterialCardView cardComposeNumbers;
    private VideoView backgroundVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selection);

        // Setup home button
        ImageButton btnHome = findViewById(R.id.btnHome);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                SoundManager.playClickSound(this);
                Intent intent = new Intent(this, MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        initializeViews();
        setupBackgroundVideo();
        setupClickListeners();
    }

    private void initializeViews() {
        cardCompareNumbers = findViewById(R.id.cardCompareNumbers);
        cardOrderNumbers = findViewById(R.id.cardOrderNumbers);
        cardComposeNumbers = findViewById(R.id.cardComposeNumbers);
        backgroundVideo = findViewById(R.id.backgroundVideo);
    }

    private void setupBackgroundVideo() {
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.background_all;
        Uri uri = Uri.parse(videoPath);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.start();
        backgroundVideo.setOnPreparedListener(mp -> mp.setLooping(true));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundVideo != null) {
            backgroundVideo.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundVideo != null) {
            backgroundVideo.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backgroundVideo != null) {
            backgroundVideo.stopPlayback();
        }
    }

    private void setupClickListeners() {
        cardCompareNumbers.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            startActivity(new Intent(this, CompareNumbersActivity.class));
        });

        cardOrderNumbers.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            startActivity(new Intent(this, OrderNumbersActivity.class));
        });

        cardComposeNumbers.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            startActivity(new Intent(this, ComposeNumbersActivity.class));
        });
    }
} 