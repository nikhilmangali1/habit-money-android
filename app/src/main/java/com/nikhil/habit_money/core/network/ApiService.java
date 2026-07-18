package com.nikhil.habit_money.core.network;

import com.nikhil.habit_money.auth.model.AuthResponse;
import com.nikhil.habit_money.auth.model.LoginRequest;
import com.nikhil.habit_money.auth.model.LogoutRequest;
import com.nikhil.habit_money.auth.model.RefreshTokenRequest;
import com.nikhil.habit_money.auth.model.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/v1/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/v1/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("api/v1/auth/refresh")
    Call<AuthResponse> refreshToken(@Body RefreshTokenRequest request);

    @POST("api/v1/auth/logout")
    Call<Void> logout(@Body LogoutRequest request);
}
