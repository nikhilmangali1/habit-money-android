package com.nikhil.habit_money.auth.model;

public class LogoutRequest {

    private String refreshToken;

    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
