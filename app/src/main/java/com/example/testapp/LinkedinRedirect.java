package com.example.testapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LinkedinRedirect extends AppCompatActivity {
    private static final String TAG = "LinkedinRedirect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Handle direct URI redirect
        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("code") != null) {
            String authorizationCode = data.getQueryParameter("code");
            exchangeCodeForAccessToken(authorizationCode);
        }

        // Handle Firebase Dynamic Links
        handleDynamicLinks();
    }

    private void handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get the deep link from the result
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        // Handle the deep link (redirect after LinkedIn OAuth)
                        if (deepLink != null && deepLink.toString().contains("auth/linkedin/callback")) {
                            // Extract the data from the deep link
                            String accessToken = deepLink.getQueryParameter("access_token");
                            if (accessToken != null) {
                                fetchLinkedInProfile(accessToken);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error processing dynamic link", e);
                        showErrorToUser("Authentication failed");
                    }
                });
    }

    private void exchangeCodeForAccessToken(String code) {
        String tokenUrl = "https://www.linkedin.com/oauth/v2/accessToken";
        String clientId = getString(R.string.LINKEDIN_CLIENT_ID);
        String clientSecret = getString(R.string.LINKEDIN_CLIENT_SECRET);
        String redirectUri = getString(R.string.LINKEDIN_REDIRECT_URI);

        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Token request failed", e);
                    showErrorToUser("Network error");
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String accessToken = jsonObject.getString("access_token");

                        runOnUiThread(() -> fetchLinkedInProfile(accessToken));
                    } else {
                        runOnUiThread(() -> showErrorToUser("Authentication failed"));
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Error parsing token response", e);
                        showErrorToUser("Error processing authentication");
                    });
                }
            }
        });
    }

    private void fetchLinkedInProfile(String accessToken) {
        String profileUrl = "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))";
        String emailUrl = "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))";

        OkHttpClient client = new OkHttpClient();

        // Profile Request
        Request profileRequest = new Request.Builder()
                .url(profileUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        // Email Request
        Request emailRequest = new Request.Builder()
                .url(emailUrl)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        // Parallel requests for profile and email
        client.newCall(profileRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Profile fetch failed", e);
                    showErrorToUser("Could not fetch profile");
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String profileResponseBody = response.body().string();
                        JSONObject profile = new JSONObject(profileResponseBody);

                        // Fetch email in parallel
                        fetchEmail(accessToken, profile);
                    } else {
                        runOnUiThread(() -> showErrorToUser("Could not fetch profile"));
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Log.e(TAG, "Error parsing profile response", e);
                        showErrorToUser("Error processing profile");
                    });
                }
            }
        });
    }

    private void fetchEmail(String accessToken, JSONObject profile) {
        OkHttpClient client = new OkHttpClient();
        Request emailRequest = new Request.Builder()
                .url("https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(emailRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                processProfileWithoutEmail(profile);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        String emailResponseBody = response.body().string();
                        JSONObject emailJson = new JSONObject(emailResponseBody);

                        // Extract email
                        String email = extractEmail(emailJson);
                        processProfileWithEmail(profile, email);
                    } else {
                        processProfileWithoutEmail(profile);
                    }
                } catch (Exception e) {
                    processProfileWithoutEmail(profile);
                }
            }
        });
    }

    private void processProfileWithEmail(JSONObject profile, String email) {
        try {
            String firstName = extractName(profile, "firstName");
            String lastName = extractName( profile, "lastName");
            String profilePictureUrl = extractProfilePictureUrl(profile);

            Intent intent = new Intent(LinkedinRedirect.this, LinkedInRegisterSeniorActivity.class);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("profilePictureUrl", profilePictureUrl);
            intent.putExtra("email", email);
            startActivity(intent);
            finish(); // Close this activity
        } catch (Exception e) {
            Log.e(TAG, "Error processing profile with email", e);
            showErrorToUser ("Error processing profile");
        }
    }

    private void processProfileWithoutEmail(JSONObject profile) {
        try {
            String firstName = extractName(profile, "firstName");
            String lastName = extractName(profile, "lastName");
            String profilePictureUrl = extractProfilePictureUrl(profile);

            Intent intent = new Intent(LinkedinRedirect.this, LinkedInRegisterSeniorActivity.class);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("profilePictureUrl", profilePictureUrl);
            intent.putExtra("email", ""); // No email available
            startActivity(intent);
            finish(); // Close this activity
        } catch (Exception e) {
            Log.e(TAG, "Error processing profile without email", e);
            showErrorToUser ("Error processing profile");
        }
    }

    private String extractEmail(JSONObject emailJson) {
        try {
            return emailJson.getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONObject("handle~")
                    .getString("emailAddress");
        } catch (Exception e) {
            Log.e(TAG, "Error extracting email", e);
            return null;
        }
    }

    private String extractName(JSONObject profile, String nameType) {
        try {
            return profile.getJSONObject(nameType)
                    .getJSONObject("localized")
                    .getString("en_US");
        } catch (Exception e) {
            Log.e(TAG, "Error extracting " + nameType, e);
            return "Unknown";
        }
    }

    private String extractProfilePictureUrl(JSONObject profile) {
        try {
            return profile.getJSONObject("profilePicture")
                    .getJSONObject("displayImage~")
                    .getJSONArray("elements")
                    .getJSONObject(0)
                    .getJSONArray("identifiers")
                    .getJSONObject(0)
                    .getString("identifier");
        } catch (Exception e) {
            Log.e(TAG, "Error extracting profile picture", e);
            return null;
        }
    }

    private void showErrorToUser (String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}