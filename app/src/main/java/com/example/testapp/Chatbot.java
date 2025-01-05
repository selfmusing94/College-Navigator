package com.example.testapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class Chatbot {
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions"; // Replace with actual Groq API URL
    private final String API_KEY;

    // Constructor accepting Context
    public Chatbot(Context context) {
        this.API_KEY = context.getString(R.string.API_KEY); // Access API key from strings.xml
    }

    // Main method to get chatbot response
    public String getResponse(String userInput) {
        // Check for topic-specific responses
        String response = getTopicResponse(userInput);

        // If the response is empty, fallback to Groq API for general conversation
        if (response.isEmpty()) {
            return callGroqAPI(userInput);
        }

        return response;
    }

    // Get topic-specific response based on user input
    private String getTopicResponse(String userInput) {
        String normalizedInput = userInput.toLowerCase();

        if (normalizedInput.contains("college") || normalizedInput.contains("university")) {
            return "Are you looking for college recommendations or information about a specific college?";
        } else if (normalizedInput.contains("cutoff")) {
            return "Please provide the college name and I'll help with the cutoff details.";
        } else if (normalizedInput.contains("review") || normalizedInput.contains("feedback")) {
            return "I can provide reviews of various colleges. Which college are you interested in?";
        } else if (normalizedInput.contains("app") || normalizedInput.contains("features") || normalizedInput.contains("about")) {
            return "This app helps you with college recommendations, cutoffs, reviews, and more. How can I assist you today?";
        }

        return ""; // Return empty if no category matched, fallback to Groq API
    }

    // Call the Groq API for general conversation when no topic-specific response is found
    private String callGroqAPI(String userInput) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(GROQ_API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);

            // Prepare the conversation history (messages array)
            JSONArray messagesArray = new JSONArray();



            // Add system message to define the AI persona
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are College Navigator AI bot, an AI assistant that helps users with information about colleges, cutoff scores, reviews, and general inquiries about the app. You were developed by Gaurav as part of an app development project and You are still under development phase. Be Specific and reply with only what you are asked with. Keep you answers short and to the point. Be Friendly, and give some crisp responses. If you are asked more about developer , guide them to the app dashboard  and then go to About Developer section to get more info. Dont repeat that u are a bot again and again. Just scold people if they are asking irrelevant things. also have a general discussion with them.The app is developed by Gaurav");
            messagesArray.put(systemMessage);

            // Add user message
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userInput);
            messagesArray.put(userMessage);

            // Create JSON payload
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("messages", messagesArray);
            jsonPayload.put("model", "llama3-70b-8192"); // Specify the model here

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonPayload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get response
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    String responseLine;
                    StringBuilder fullResponse = new StringBuilder();
                    while ((responseLine = br.readLine()) != null) {
                        fullResponse.append(responseLine.trim());
                    }

                    // Parse JSON response and extract assistant's message
                    JSONObject jsonResponse = new JSONObject(fullResponse.toString());
                    JSONArray choices = jsonResponse.getJSONArray("choices");
                    JSONObject choice = choices.getJSONObject(0);
                    String assistantMessage = choice.getJSONObject("message").getString("content");

                    // Return assistant's message
                    return assistantMessage;
                }
            } else {
                // Handle error response
                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                    String responseLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    Log.e(TAG, "Error response: " + errorResponse.toString());
                }
                return "Error: Received non-OK response from server. Code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while processing your request.";
        }
    }
}
