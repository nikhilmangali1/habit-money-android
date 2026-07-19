package com.nikhil.habit_money.auth.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nikhil.habit_money.auth.model.AuthResponse;
import com.nikhil.habit_money.auth.model.GoogleLoginRequest;
import com.nikhil.habit_money.auth.model.LoginRequest;
import com.nikhil.habit_money.auth.model.LogoutRequest;
import com.nikhil.habit_money.auth.model.RefreshTokenRequest;
import com.nikhil.habit_money.auth.model.RegisterRequest;
import com.nikhil.habit_money.core.network.ApiService;
import com.nikhil.habit_money.core.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;
    private final TokenManager tokenManager;

    public AuthRepository(ApiService apiService, TokenManager tokenManager) {
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    public void register(String firstName, String lastName, String email, String password,
                         AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
        apiService.register(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    tokenManager.saveTokens(
                            auth.getAccessToken(),
                            auth.getRefreshToken(),
                            auth.getEmail(),
                            auth.getFirstName()
                    );
                    callback.onSuccess(auth);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    private String extractErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JsonObject json = new Gson().fromJson(errorJson, JsonObject.class);
                if (json != null && json.has("message")) {
                    return json.get("message").getAsString();
                }
            }
        } catch (Exception ignored) {}
        return "Something went wrong";
    }

    public void login(String email, String password, AuthCallback callback) {
        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    tokenManager.saveTokens(
                            auth.getAccessToken(),
                            auth.getRefreshToken(),
                            auth.getEmail(),
                            auth.getFirstName()
                    );
                    callback.onSuccess(auth);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void googleLogin(String idToken, AuthCallback callback) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        apiService.googleLogin(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    tokenManager.saveTokens(
                            auth.getAccessToken(),
                            auth.getRefreshToken(),
                            auth.getEmail(),
                            auth.getFirstName()
                    );
                    callback.onSuccess(auth);
                } else {
                    callback.onError(extractErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void refreshToken(AuthCallback callback) {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            callback.onError("No refresh token available");
            return;
        }
        RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
        apiService.refreshToken(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    tokenManager.saveTokens(
                            auth.getAccessToken(),
                            auth.getRefreshToken(),
                            auth.getEmail(),
                            auth.getFirstName()
                    );
                    callback.onSuccess(auth);
                } else {
                    callback.onError("Session expired. Please login again.");
                    tokenManager.clearAll();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void logout(AuthCallback callback) {
        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null) {
            tokenManager.clearAll();
            callback.onSuccess(null);
            return;
        }
        LogoutRequest request = new LogoutRequest(refreshToken);
        apiService.logout(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tokenManager.clearAll();
                callback.onSuccess(null);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                tokenManager.clearAll();
                callback.onSuccess(null);
            }
        });
    }
}
