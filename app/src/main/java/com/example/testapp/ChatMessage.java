package com.example.testapp;

import java.util.Date;

public class ChatMessage {

        private String content;
        private MessageType type;
        private Date timestamp;

        public ChatMessage(String content, MessageType type) {
            this.content = content;
            this.type = type;
            this.timestamp = new Date();
        }

        // Getters and Setters
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public MessageType getType() {
            return type;
        }

        public void setType(MessageType type) {
            this.type = type;
        }

        public Date getTimestamp() {
            return timestamp;
        }

}


