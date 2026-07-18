package com.nikhil.habit_money.auth.model;

public class AuthResponse {

    private String userId;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String authProvider;
    private String tokenType;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUserId() { return userId; }
    public String getRole() { return role; }
    public String getAuthProvider() { return authProvider; }
}
