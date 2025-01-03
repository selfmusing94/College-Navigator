package com.example.testapp;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AIChatbotActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton microphoneButton;
    private RecyclerView chatRecyclerView;
    private AIChatAdapter chatAdapter;
    private AIChatViewModel chatViewModel;
    private List<ChatMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat_bot);

        // Initialize components
        initializeViews();
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        observeViewModel();
    }

    private void initializeViews() {
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        microphoneButton = findViewById(R.id.microphoneButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        // Initially disable send button
        sendButton.setEnabled(false);
    }

    private void setupViewModel() {
        chatViewModel = new ViewModelProvider(this).get(AIChatViewModel.class);
        messageList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        chatAdapter = new AIChatAdapter(messageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        // Send button click listener
        sendButton.setOnClickListener(v -> sendMessage());

        // Microphone button click listener
        microphoneButton.setOnClickListener(v -> startVoiceRecognition());

        // Message input text watcher
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(s.toString().trim().length() > 0);
            }
        });
    }

    private void observeViewModel() {
        chatViewModel.chatMessages.observe(this, messages -> {
            // Update message list and notify adapter
            messageList.clear();
            messageList.addAll(messages);
            chatAdapter.notifyDataSetChanged();

            // Scroll to bottom
            if (!messageList.isEmpty()) {
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        chatViewModel.isLoading.observe(this, isLoading -> {
            sendButton.setEnabled(!isLoading);
        });
    }


    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatViewModel.sendMessage(message);
            messageInput.setText("");
        }
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your message");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            if (data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    messageInput.setText(spokenText);
                }
            }
        }
    }
}