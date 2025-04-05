package com.example.mathematics;


import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.android.material.button.MaterialButton;


import java.util.Random;

public class CompareNumbersActivity extends BaseActivity {
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvResult;
    private MaterialButton btnNext;
    private VideoView backgroundVideo;
    private int number1;
    private int number2;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_numbers);

        // Setup home button
        ImageButton btnHome = findViewById(R.id.btnHome);
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                SoundManager.playClickSound(this);
                Intent intent = new Intent(this, TopicSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        random = new Random();
        initializeViews();
        setupBackgroundVideo();
        setupClickListeners();

        generateNewQuestion();
    }

    private void initializeViews() {
        tvNumber1 = findViewById(R.id.tvNumber1);
        tvNumber2 = findViewById(R.id.tvNumber2);
        tvResult = findViewById(R.id.tvResult);
        btnNext = findViewById(R.id.btnNext);
        backgroundVideo = findViewById(R.id.backgroundVideo);
    }

    private void setupBackgroundVideo() {
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.background_all;
        Uri uri = Uri.parse(videoPath);
        backgroundVideo.setVideoURI(uri);
        backgroundVideo.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            // Set video scaling mode to fit the screen while cropping as necessary
            mp.setVideoScalingMode(
                MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            );
        });
        backgroundVideo.start();
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
        tvNumber1.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            checkAnswer(number1);
        });
        tvNumber2.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            checkAnswer(number2);
        });
        btnNext.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            generateNewQuestion();
        });
    }

    private void generateNewQuestion() {
        // Generate random numbers between 10 and 99
        number1 = random.nextInt(90) + 10;
        do {
            number2 = random.nextInt(90) + 10;
        } while (number2 == number1);

        tvNumber1.setText(String.valueOf(number1));
        tvNumber2.setText(String.valueOf(number2));
        findViewById(R.id.cardResult).setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        tvNumber1.setEnabled(true);
        tvNumber2.setEnabled(true);
    }


    private void checkAnswer(int selectedNumber) {
        tvNumber1.setEnabled(false);
        tvNumber2.setEnabled(false);
        btnNext.setVisibility(View.VISIBLE);

        if ((selectedNumber == number1 && number1 > number2) ||
            (selectedNumber == number2 && number2 > number1)) {
            // Correct answer
            String message;
            if (selectedNumber == number1) {
                message = "Great job! " + number1 + " is bigger than " + number2 + "!";
            } else {
                message = "Great job! " + number2 + " is bigger than " + number1 + "!";
            }
            tvResult.setText(message);
            tvResult.setTextColor(getResources().getColor(R.color.primary));
            showCongratulationVideo();
        } else {
            // Wrong answer
            String message;
            if (selectedNumber == number1) {
                message = "Oops! " + number1 + " is smaller than " + number2 + ". Try again!";
            } else {
                message = "Oops! " + number2 + " is smaller than " + number1 + ". Try again!";
            }
            tvResult.setText(message);
            tvResult.setTextColor(getResources().getColor(R.color.secondary));
        }
        findViewById(R.id.cardResult).setVisibility(View.VISIBLE);
    }

    private void showCongratulationVideo() {
        ViewGroup rootView = findViewById(android.R.id.content);
        View popupView = getLayoutInflater().inflate(R.layout.popup_congratulation, rootView, false);
        
        // Set initial scale to 0 for animation
        popupView.setScaleX(0f);
        popupView.setScaleY(0f);
        
        rootView.addView(popupView);
        
        // Animate the popup
        popupView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .start();

        VideoView videoView = popupView.findViewById(R.id.congratulationVideo);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.congratulation;
        videoView.setVideoURI(Uri.parse(videoPath));
        
        videoView.setOnPreparedListener(mp -> mp.setLooping(false));
        videoView.setOnCompletionListener(mp -> {
            // Animate out and remove the popup
            popupView.animate()
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(300)
                .withEndAction(() -> rootView.removeView(popupView))
                .start();
        });
        
        videoView.start();
    }
} 