package com.example.testapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AIChatAdapter extends RecyclerView.Adapter<AIChatAdapter.ChatViewHolder> {

    private final List<ChatMessage> messages;
    private final Set<Integer> animatedPositions = new HashSet<>();

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
        ChatMessage message = messages.get(position);

        boolean shouldAnimate = message.getType() == MessageType.AI &&
                !animatedPositions.contains(position);

        holder.bind(message, shouldAnimate);

        if (shouldAnimate) {
            animatedPositions.add(position);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void resetAnimationTracking() {
        animatedPositions.clear();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final FrameLayout aiIconContainer, userIconContainer;
        private final CardView messageContainer;
        private final View typingDot;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            aiIconContainer = itemView.findViewById(R.id.aiIconContainer);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            userIconContainer = itemView.findViewById(R.id.userIconContainer);
            typingDot = itemView.findViewById(R.id.typingDots);
        }

        public void bind(ChatMessage message, boolean shouldAnimate) {
            resetStyles();

            if (message.getType() == MessageType.USER) {
                configureUserMessageStyle(message.getContent());
            } else {
                configureAIMessageStyle(message.getContent(), shouldAnimate);
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

        private void configureAIMessageStyle(String content, boolean shouldAnimate) {
            aiIconContainer.setVisibility(View.VISIBLE);
            userIconContainer.setVisibility(View.GONE);
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.ai_message_background)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.ai_text_color)
            );

            if (shouldAnimate) {
                startBigDotAnimation(typingDot);
                startTypingAnimation(content, () -> {
                    typingDot.setVisibility(View.GONE);
                });
            } else {
                typingDot.setVisibility(View.GONE);
                messageTextView.setText(content);
            }
        }

        private void startBigDotAnimation(View typingDot) {
            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1f, 1.5f, 1f);
            scaleAnimator.setDuration(700);
            scaleAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleAnimator.setRepeatMode(ValueAnimator.RESTART);
            scaleAnimator.addUpdateListener(animation -> {
                float scale = (float) animation.getAnimatedValue();
                typingDot.setScaleX(scale);
                typingDot.setScaleY(scale);
            });

            typingDot.setVisibility(View.VISIBLE);
            scaleAnimator.start();
        }

        private void startTypingAnimation(String content, Runnable onAnimationEnd) {
            messageTextView.setText("");

            ValueAnimator animator = ValueAnimator.ofInt(0, content.length());
            animator.setDuration(content.length() * 50L);
            animator.addUpdateListener(animation -> {
                int charCount = (int) animation.getAnimatedValue();
                String currentText = content.substring(0, charCount);

                messageTextView.setText(currentText);
                typingDot.setVisibility(View.VISIBLE);
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
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