package com.example.testapp;

import android.animation.ValueAnimator;
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
        private AITypingAnimation typingAnimationUtil;

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
        holder.bind(message, position == messages.size() - 1); // Pass whether this is the latest message
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final FrameLayout aiIconContainer , userIconContainer;
        private final CardView messageContainer;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            aiIconContainer = itemView.findViewById(R.id.aiIconContainer);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            userIconContainer = itemView.findViewById(R.id.userIconContainer);
        }

        public void bind(ChatMessage message, boolean isLatestMessage) {
            // Reset styles for recycling
            resetStyles();

            if (message.getType() == MessageType.USER) {
                // User message styling
                configureUserMessageStyle(message.getContent());
            } else {
                // AI message styling
                configureAIMessageStyle(message.getContent(), isLatestMessage);
            }
        }

        private void resetStyles() {
            aiIconContainer.setVisibility(View.GONE); // Default hide AI icon
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.background_light)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.user_message_background)
            );
        }

        private void configureUserMessageStyle(String content) {
            // Show user icon and hide AI icon
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
            // Hide user icon and show AI icon
            aiIconContainer.setVisibility(View.VISIBLE);
            userIconContainer.setVisibility(View.GONE);
            messageContainer.setCardBackgroundColor(
                    itemView.getContext().getColor(R.color.ai_message_background)
            );
            messageTextView.setTextColor(
                    itemView.getContext().getColor(R.color.ai_text_color)
            );

            if (isLatestMessage) {
                // Only apply animation to the latest AI message
                startTypingAnimation(content);
            } else {
                // Directly set text for previous messages
                messageTextView.setText(content);
            }
        }

        private void startTypingAnimation(String content) {
            messageTextView.setText(""); // Clear text initially
            messageTextView.setHint("|"); // Add cursor effect

            ValueAnimator animator = ValueAnimator.ofInt(0, content.length());
            animator.setDuration(content.length() * 35L); // Adjust typing speed
            animator.addUpdateListener(animation -> {
                int charCount = (int) animation.getAnimatedValue();
                messageTextView.setText(content.substring(0, charCount));
                if (charCount == content.length()) {
                    messageTextView.setHint(""); // Remove cursor when done
                }
            });
            animator.start();
        }
    }
}
