package com.example.smartparking1;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartparking1.PaymentReciept;

public class PaymentSuccessful extends AppCompatActivity {

    private LottieAnimationView animationView;
    private ValueAnimator animator;
    private Handler handler;
    private Runnable redirectRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successfu);
        animationView = findViewById(R.id.lottieAnimationView);
        startCheckAnimation();
        stopAnimationAfterDelay(5000);
    }

    private void startCheckAnimation() {
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(5000);
        animator.addUpdateListener(valueAnimator -> animationView.setProgress((Float) valueAnimator.getAnimatedValue()));

        if (animationView.getProgress() == 0f) {
            animator.start();
        } else {
            animationView.setProgress(0f);
        }
    }

    private void stopAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    private void stopAnimationAfterDelay(long delayMillis) {
        handler = new Handler();
        redirectRunnable = () -> redirectToNextActivity();
        handler.postDelayed(redirectRunnable, delayMillis);
    }

    private void redirectToNextActivity() {
        Intent intent = new Intent(PaymentSuccessful.this, Secondpage.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the redirectRunnable from the handler if the activity is destroyed
        if (handler != null && redirectRunnable != null) {
            handler.removeCallbacks(redirectRunnable);
        }
    }
}
