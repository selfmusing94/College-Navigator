package com.example.testapp;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class AITypingAnimation {
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView textView;
    private String fullText;
    private int currentIndex = 0;
    private OnTypingCompleteListener listener;

    public interface OnTypingCompleteListener {
        void onTypingComplete();
    }

    public AITypingAnimation(TextView textView) {
        this.textView = textView;
    }

    public void animateText(String text) {
        animateText(text, null);
    }

    public void animateText(String text, OnTypingCompleteListener listener) {
        this.fullText = text;
        this.currentIndex = 0;
        this.listener = listener;
        this.textView.setText("");
        startTyping();
    }

    private void startTyping() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentIndex < fullText.length()) {
                    textView.append(fullText.substring(currentIndex, currentIndex + 1));
                    currentIndex++;
                    startTyping();
                } else {
                    if (listener != null) {
                        listener.onTypingComplete();
                    }
                }
            }
        }, getTypingDelay());
    }

    private long getTypingDelay() {
        // Randomize typing speed to make it more natural
        return (long) (20 + Math.random() * 30);
    }

    public void cancel() {
        handler.removeCallbacksAndMessages(null);
    }
}