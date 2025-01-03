package com.example.testapp;

import java.util.Random;

public class AIRepository {

    private static final String[] AI_PERSONALITIES = {
            "As an AI assistant, ",
            "Based on my analysis, ",
            "Let me help you with that. ",
            "Here's a comprehensive response: "
    };

    private static final String[] RESPONSE_STYLES = {
            "I'd be happy to provide insights on that topic. ",
            "Let me break down the key points for you. ",
            "Here's a detailed explanation: ",
            "I'll help you understand this thoroughly. "
    };

    public String generateResponse(String message) {
        // Simulate more intelligent response generation
        return generateContextualResponse(message);
    }

    private String generateContextualResponse(String message) {
        Random random = new Random();

        // Basic response structure
        String personality = AI_PERSONALITIES[random.nextInt(AI_PERSONALITIES.length)];
        String responseStyle = RESPONSE_STYLES[random.nextInt(RESPONSE_STYLES.length)];

        // Simple response generation logic
        String response = personality + responseStyle;

        // Add some context-based variation
        if (message.toLowerCase().contains("help")) {
            response += "I'm designed to assist you with a wide range of tasks and questions.";
        } else if (message.toLowerCase().contains("how")) {
            response += "The process involves several key steps that I can explain in detail.";
        } else if (message.toLowerCase().contains("what")) {
            response += "That's a great question! Let me provide you with some insights.";
        } else {
            response += "Feel free to ask me anything else!";
        }

        return response;
    }
}