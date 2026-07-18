package com.nikhil.habit_money.auth.repository;

import com.nikhil.habit_money.auth.model.AuthResponse;

public interface AuthCallback {
    void onSuccess(AuthResponse response);
    void onError(String message);
}
