package com.example.mathematics;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderNumbersActivity extends BaseActivity {
    private TextView tvRandom1;
    private TextView tvRandom2;
    private TextView tvRandom3;
    private TextView tvNumber1;
    private TextView tvNumber2;
    private TextView tvNumber3;
    private TextView tvResult;
    private MaterialButton btnCheck;
    private MaterialButton btnNext;
    private MaterialCardView cardSmallestCard;
    private MaterialCardView cardMediumCard;
    private MaterialCardView cardBiggestCard;
    private VideoView backgroundVideo;
    private ScrollView scrollView;
    private List<Integer> numbers;
    private List<Integer> correctOrder;
    private Random random;
    private float touchStartX;
    private float touchStartY;
    private long touchStartTime;
    private static final float DRAG_THRESHOLD = 50f; // Increased minimum distance to start drag
    private static final long SCROLL_DELAY = 500; // Delay before allowing scroll (in milliseconds)
    private boolean isDragging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_numbers);

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
    }

    private void initializeViews() {
        tvRandom1 = findViewById(R.id.tvRandom1);
        tvRandom2 = findViewById(R.id.tvRandom2);
        tvRandom3 = findViewById(R.id.tvRandom3);
        tvNumber1 = findViewById(R.id.tvNumber1);
        tvNumber2 = findViewById(R.id.tvNumber2);
        tvNumber3 = findViewById(R.id.tvNumber3);
        tvResult = findViewById(R.id.tvResult);
        btnCheck = findViewById(R.id.btnCheck);
        btnNext = findViewById(R.id.btnNext);
        cardSmallestCard = findViewById(R.id.cardSmallestCard);
        cardMediumCard = findViewById(R.id.cardMediumCard);
        cardBiggestCard = findViewById(R.id.cardBiggestCard);
        backgroundVideo = findViewById(R.id.backgroundVideo);
        scrollView = findViewById(R.id.scrollView);
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
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartX = event.getRawX();
                    touchStartY = event.getRawY();
                    touchStartTime = System.currentTimeMillis();
                    isDragging = false;
                    // Temporarily disable scroll
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = Math.abs(event.getRawX() - touchStartX);
                    float deltaY = Math.abs(event.getRawY() - touchStartY);
                    
                    if (!isDragging && (deltaX > DRAG_THRESHOLD || deltaY > DRAG_THRESHOLD)) {
                        isDragging = true;
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(null, shadowBuilder, v, 0);
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // Re-enable scroll after a delay if not dragging
                    if (!isDragging) {
                        long touchDuration = System.currentTimeMillis() - touchStartTime;
                        if (touchDuration < SCROLL_DELAY) {
                            scrollView.postDelayed(() -> 
                                scrollView.requestDisallowInterceptTouchEvent(false), 
                                SCROLL_DELAY - touchDuration
                            );
                        } else {
                            scrollView.requestDisallowInterceptTouchEvent(false);
                        }
                    }
                    return true;
            }
            return false;
        };

        // Set up touch listeners for random number TextViews
        tvRandom1.setOnTouchListener(touchListener);
        tvRandom2.setOnTouchListener(touchListener);
        tvRandom3.setOnTouchListener(touchListener);

        // Set up drop listeners for cards
        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setAlpha(0.7f);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    // Re-enable scroll when drag ends
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    return true;
                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    TextView draggedTextView = (TextView) draggedView;
                    TextView targetTextView = (TextView) ((MaterialCardView) v).getChildAt(0);
                    
                    // Only allow dropping if the target is empty
                    if (targetTextView.getText().toString().isEmpty()) {
                        // Move the text
                        targetTextView.setText(draggedTextView.getText().toString());
                        draggedTextView.setText("");
                    }
                    return true;
            }
            return true;
        };

        cardSmallestCard.setOnDragListener(dragListener);
        cardMediumCard.setOnDragListener(dragListener);
        cardBiggestCard.setOnDragListener(dragListener);
    }

    private void setupClickListeners() {
        btnCheck.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            checkOrder();
        });
        btnNext.setOnClickListener(v -> {
            SoundManager.playClickSound(this);
            generateNewQuestion();
        });
    }

    private void generateNewQuestion() {
        numbers = new ArrayList<>();
        // Generate 3 unique random numbers between 10 and 99
        while (numbers.size() < 3) {
            int num = random.nextInt(90) + 10;
            if (!numbers.contains(num)) {
                numbers.add(num);
            }
        }

        // Create a copy for correct order
        correctOrder = new ArrayList<>(numbers);
        Collections.sort(correctOrder);

        // Shuffle the numbers for display
        Collections.shuffle(numbers);

        // Display the random numbers
        tvRandom1.setText(String.valueOf(numbers.get(0)));
        tvRandom2.setText(String.valueOf(numbers.get(1)));
        tvRandom3.setText(String.valueOf(numbers.get(2)));

        // Clear the target boxes
        tvNumber1.setText("");
        tvNumber2.setText("");
        tvNumber3.setText("");

        findViewById(R.id.cardResult).setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
    }

    private void checkOrder() {
        List<Integer> currentOrder = new ArrayList<>();
        String smallestText = tvNumber1.getText().toString();
        String mediumText = tvNumber2.getText().toString();
        String biggestText = tvNumber3.getText().toString();

        if (smallestText.isEmpty() || mediumText.isEmpty() || biggestText.isEmpty()) {
            tvResult.setText("Please fill all boxes!");
            tvResult.setTextColor(getResources().getColor(R.color.secondary));
            findViewById(R.id.cardResult).setVisibility(View.VISIBLE);
            return;
        }

        currentOrder.add(Integer.parseInt(smallestText));
        currentOrder.add(Integer.parseInt(mediumText));
        currentOrder.add(Integer.parseInt(biggestText));

        boolean isCorrect = currentOrder.equals(correctOrder);

        if (isCorrect) {
            tvResult.setText("Correct! Well done!");
            tvResult.setTextColor(getResources().getColor(R.color.primary));
            showCongratulationVideo();
        } else {
            tvResult.setText("Try again!");
            tvResult.setTextColor(getResources().getColor(R.color.secondary));
        }

        findViewById(R.id.cardResult).setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.GONE);
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
} 