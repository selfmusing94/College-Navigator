package com.example.testapp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIChatViewModel extends AndroidViewModel {
    // Logging tag
    private static final String TAG = "ChatViewModel";
    private final long responseDelay = 1500;

    private Chatbot chatbot;

    // LiveData for chat messages
    private final MutableLiveData<List<ChatMessage>> _chatMessages =
            new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ChatMessage>> chatMessages = _chatMessages;

    // LiveData for loading state
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    // LiveData for error handling
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    // LiveData for network connectivity
    private final MutableLiveData<Boolean> _isNetworkAvailable = new MutableLiveData<>(true);
    public LiveData<Boolean> isNetworkAvailable = _isNetworkAvailable;

    // Repositories and Services
    private final AIRepository aiRepository;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    // Context for system services
    private final ConnectivityManager connectivityManager;

    // Message Queue for handling multiple requests
    private final List<String> messageQueue = new ArrayList<>();
    private boolean isProcessing = false;

    public AIChatViewModel(@NonNull Application application) {
        super(application);

        // Initialize dependencies
        aiRepository = new AIRepository();
        executorService = Executors.newFixedThreadPool(3);
        mainHandler = new Handler(Looper.getMainLooper());

        // Get system services
        connectivityManager = (ConnectivityManager)
                application.getSystemService(Application.CONNECTIVITY_SERVICE);

        // Check initial network state
        checkNetworkAvailability();

        // Instantiate the chatbot
        chatbot = new Chatbot(getApplication());
    }

    // Send message method with comprehensive error handling
    public void sendMessage(String message) {
        // Validate message
        if (message == null || message.trim().isEmpty()) {
            _errorMessage.setValue("Message cannot be empty");
            return;
        }

        // Check network availability
        if (Boolean.FALSE.equals(isNetworkAvailable.getValue())) {
            _errorMessage.setValue("No internet connection");
            return;
        }

        // Add message to queue
        messageQueue.add(message);

        // Process messages if not already processing
        if (!isProcessing) {
            processMessageQueue();
        }
    }

    // Process message queue
    private void processMessageQueue() {
        if (messageQueue.isEmpty()) {
            isProcessing = false;
            return;
        }

        isProcessing = true;
        String message = messageQueue.remove(0);

        // Add user message
        addMessageToChat(message, MessageType.USER);

        // Set loading state
        _isLoading.setValue(true);

        // Process message asynchronously
        executorService.execute(() -> {
            try {
                // Simulate processing time with some randomness
                Thread.sleep(getRandomProcessingTime());

                // Generate AI response using the Chatbot class
                String aiResponse = chatbot.getResponse(message);

                // Post response on main thread
                mainHandler.post(() -> {
                    addMessageToChat(aiResponse, MessageType.AI);
                    _isLoading.setValue(false);

                    // Process next message in queue
                    processMessageQueue();
                });
            } catch (Exception e) {
                handleError(e);
            }
        });
    }

    // Add message to chat list
    private void addMessageToChat(String message, MessageType type) {
        List<ChatMessage> currentMessages = new ArrayList<>(_chatMessages.getValue());
        ChatMessage chatMessage = new ChatMessage(message, type);
        currentMessages.add(chatMessage);
        _chatMessages.setValue(currentMessages);
    }

    // Simulate variable processing time
    private long getRandomProcessingTime() {
        return (long) (800 + Math.random() * 1500); // 800-2300 ms
    }

    // Show typing indicator
    private void showTypingIndicator() {
        ChatMessage typingIndicatorMessage = new ChatMessage("...", MessageType.AI);
        addMessageToChat(typingIndicatorMessage.getContent(), MessageType.AI);
    }

    // Remove typing indicator
    private void removeTypingIndicator() {
        List<ChatMessage> currentMessages = _chatMessages.getValue();
        if (currentMessages != null && !currentMessages.isEmpty() && currentMessages.get(currentMessages.size() - 1).getContent().equals("...")) {
            currentMessages.remove(currentMessages.size() - 1);
            _chatMessages.setValue(currentMessages);
        }
    }

    // Error handling method
    private void handleError(Exception e) {
        mainHandler.post(() -> {
            _isLoading.setValue(false);
            _errorMessage.setValue("An error occurred: " + e.getMessage());
            Log.e(TAG, "Chat error", e);
        });
    }

    // Check network availability
    public void checkNetworkAvailability() {
        executorService.execute(() -> {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            mainHandler.post(() -> {
                _isNetworkAvailable.setValue(isConnected);
                if (!isConnected) {
                    _errorMessage.setValue("No internet connection");
                }
            });
        });
    }

    // Clear chat history
    public void clearChatHistory() {
        _chatMessages.setValue(new ArrayList<>());
    }

    // Get last message
    public ChatMessage getLastMessage() {
        List<ChatMessage> messages = _chatMessages.getValue();
        return messages != null && !messages.isEmpty()
                ? messages.get(messages.size() - 1)
                : null;
    }

    // Retry last message
    public void retryLastMessage() {
        ChatMessage lastUserMessage = findLastUserMessage();
        if (lastUserMessage != null) {
            sendMessage(lastUserMessage.getContent());
        }
    }

    // Find last user message
    private ChatMessage findLastUserMessage() {
        List<ChatMessage> messages = _chatMessages.getValue();
        if (messages != null) {
            for (int i = messages.size() - 1; i >= 0; i--) {
                if (messages.get(i).getType() == MessageType.USER) {
                    return messages.get(i);
                }
            }
        }
        return null;
    }

    // Lifecycle cleanup
    @Override
    protected void onCleared() {
        super.onCleared();
        // Shutdown executor service
        executorService.shutdownNow();
    }
}
