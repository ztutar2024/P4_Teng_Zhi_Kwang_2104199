package com.example.mathematics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.Random;

public class ComposeNumbersActivity extends BaseActivity {
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvNumber3;
    private TextView tvTarget1;
    private TextView tvTarget2;
    private TextView tvResult;
    private MaterialButton btnCheck;
    private MaterialButton btnNext;
    private VideoView backgroundVideo;
    private int targetNumber;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_numbers);

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
        setupDragAndDrop();
        setupClickListeners();
        generateNewQuestion();

        // Show tutorial overlay
        showTutorialOverlay();
    }

    private void initializeViews() {
        tvNumber1 = findViewById(R.id.tvNumber1);
        tvNumber2 = findViewById(R.id.tvNumber2);
        tvNumber3 = findViewById(R.id.tvNumber3);
        tvTarget1 = findViewById(R.id.tvTarget1);
        tvTarget2 = findViewById(R.id.tvTarget2);
        tvResult = findViewById(R.id.tvResult);
        btnCheck = findViewById(R.id.btnCheck);
        btnNext = findViewById(R.id.btnNext);
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

    private void setupDragAndDrop() {
        View.OnTouchListener touchListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(null, shadowBuilder, v, 0);
                return true;
            }
            return false;
        };

        tvNumber1.setOnTouchListener(touchListener);
        tvNumber2.setOnTouchListener(touchListener);
        tvNumber3.setOnTouchListener(touchListener);
        tvTarget1.setOnTouchListener(touchListener);
        tvTarget2.setOnTouchListener(touchListener);

        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setAlpha(0.7f);
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.5f);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(0.7f);
                    return true;
                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    TextView draggedTextView = (TextView) draggedView;
                    TextView targetTextView = (TextView) v;

                    if (targetTextView.getText().toString().isEmpty()) {
                        targetTextView.setText(draggedTextView.getText().toString());
                        draggedTextView.setText("");
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    return true;
            }
            return false;
        };

        tvTarget1.setOnDragListener(dragListener);
        tvTarget2.setOnDragListener(dragListener);
        tvNumber1.setOnDragListener(dragListener);
        tvNumber2.setOnDragListener(dragListener);
        tvNumber3.setOnDragListener(dragListener);
    }

    private void setupClickListeners() {
        btnCheck.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            checkAnswer();
        });
        btnNext.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            generateNewQuestion();
        });
    }

    private void generateNewQuestion() {
        // Generate a target number between 10 and 18
        targetNumber = random.nextInt(9) + 10;
        ((TextView) findViewById(R.id.tvQuestion)).setText("Can you make " + targetNumber + "?");

        // Generate two single-digit numbers that sum to the target number
        int num1 = random.nextInt(targetNumber - 1) + 1; // Ensure num1 is at least 1
        int num2 = targetNumber - num1;

        // Generate a third random single-digit number that does not sum to the target with any other number
        int num3;
        do {
            num3 = random.nextInt(9) + 1; // Generate a single-digit number
        } while (num3 == num1 || num3 == num2 || num3 + num1 == targetNumber || num3 + num2 == targetNumber);

        // Assign the numbers to the TextViews
        tvNumber1.setText(String.valueOf(num1));
        tvNumber2.setText(String.valueOf(num2));
        tvNumber3.setText(String.valueOf(num3));

        // Clear previous inputs
        tvTarget1.setText("");
        tvTarget2.setText("");

        findViewById(R.id.cardResult).setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
    }

    private void checkAnswer() {
        String num1Str = tvTarget1.getText().toString();
        String num2Str = tvTarget2.getText().toString();

        if (num1Str.isEmpty() || num2Str.isEmpty()) {
            return;
        }

        int num1 = Integer.parseInt(num1Str);
        int num2 = Integer.parseInt(num2Str);

        if (num1 + num2 == targetNumber) {
            tvResult.setText("Great job! You made the number " + targetNumber + "!");
            tvResult.setTextColor(getResources().getColor(R.color.primary));
            showCongratulationVideo();
        } else {
            int sum = num1 + num2;
            if (sum < targetNumber) {
                tvResult.setText("That makes " + sum + ". Try bigger numbers!");
            } else {
                tvResult.setText("That makes " + sum + ". Try smaller numbers!");
            }
            tvResult.setTextColor(getResources().getColor(R.color.secondary));
        }

        findViewById(R.id.cardResult).setVisibility(View.VISIBLE);
        btnNext.setVisibility(View.VISIBLE);
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

    private void showTutorialOverlay() {
        final ViewGroup rootView = findViewById(android.R.id.content);
        final View overlayView = getLayoutInflater().inflate(R.layout.overlay_tutorial, rootView, false);
        rootView.addView(overlayView);

        Button btnCloseTutorial = overlayView.findViewById(R.id.btnCloseTutorial);
        btnCloseTutorial.setOnClickListener(v -> rootView.removeView(overlayView));
    }
} 