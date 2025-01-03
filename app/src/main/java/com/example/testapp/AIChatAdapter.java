package com.example.testapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AIChatAdapter extends RecyclerView.Adapter<AIChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;
    private boolean isTypingAnimationCompleted = false; // Flag for animation completion

    public AIChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ai_chatbot, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        if (position == messages.size() - 1) {
            isTypingAnimationCompleted = false; // Reset for the latest message
        }

        ChatMessage message = messages.get(position);
        holder.bind(message, position == messages.size() - 1); // Check if it's the latest message
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final FrameLayout aiIconContainer, userIconContainer;
        private final CardView messageContainer;
        private final View typingDot;
        private final Handler handler = new Handler(); // Single instance of Handler for dots animation

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            aiIconContainer = itemView.findViewById(R.id.aiIconContainer);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            userIconContainer = itemView.findViewById(R.id.userIconContainer);
            typingDot = itemView.findViewById(R.id.typingDots);
        }

        public void bind(ChatMessage message, boolean isLatestMessage) {
            resetStyles();

            if (message.getType() == MessageType.USER) {
                configureUserMessageStyle(message.getContent());
            } else {
                configureAIMessageStyle(message.getContent(), isLatestMessage);
            }
        }

        private void resetStyles() {
            aiIconContainer.setVisibility(View.GONE);
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.background_light)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.user_message_background)
            );
        }

        private void configureUserMessageStyle(String content) {
            userIconContainer.setVisibility(View.VISIBLE);
            aiIconContainer.setVisibility(View.GONE);
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.user_message_background)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.user_text_color)
            );
            messageTextView.setText(content);
        }

        private void configureAIMessageStyle(String content, boolean isLatestMessage) {
            aiIconContainer.setVisibility(View.VISIBLE);
            userIconContainer.setVisibility(View.GONE);
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.ai_message_background)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.ai_text_color)
            );

            if (isLatestMessage) {
                // Start both animations: typing dots and text
                startBigDotAnimation(typingDot);
                startTypingAnimation(content, () -> {
                    // Stop dots animation and hide when typing is done
                    typingDot.setVisibility(View.GONE);
                });
            } else {
                // Directly set text for older messages
                typingDot.setVisibility(View.GONE);
                messageTextView.setText(content);
            }
        }

        private void startBigDotAnimation(View typingDot) {
            // Animate the dot with a pulse effect
            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f); // Scale up and down
            scaleAnimator.setDuration(700); // Animation duration
            scaleAnimator.setRepeatCount(ValueAnimator.INFINITE); // Infinite loop
            scaleAnimator.setRepeatMode(ValueAnimator.RESTART); // Restart animation
            scaleAnimator.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();
                typingDot.setScaleX(scale);
                typingDot.setScaleY(scale);
            });

            typingDot.setVisibility(View.VISIBLE); // Ensure the dot is visible
            scaleAnimator.start();
        }

        private void startTypingAnimation(String content, Runnable onAnimationEnd) {
            messageTextView.setText(""); // Clear text initially

            ValueAnimator animator = ValueAnimator.ofInt(0, content.length());
            animator.setDuration(content.length() * 50L); // Adjust typing speed
            animator.addUpdateListener(animation -> {
                int charCount = (int) animation.getAnimatedValue();
                String currentText = content.substring(0, charCount);

                // Update the text as it types
                messageTextView.setText(currentText);

                // Align dots at the end of the last visible line
                typingDot.setVisibility(View.VISIBLE);
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // Hide typing dots after the message finishes typing
                    typingDot.setVisibility(View.GONE);
                    if (onAnimationEnd != null) onAnimationEnd.run();
                }

                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });

            animator.start();
        }

    }
}
